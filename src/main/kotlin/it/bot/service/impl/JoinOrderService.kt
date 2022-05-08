package it.bot.service.impl

import it.bot.model.command.JoinOrderCommand
import it.bot.model.entity.OrderEntity
import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.model.messages.OrderMessages
import it.bot.repository.OrderRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class JoinOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    @Inject private val orderRepository: OrderRepository,
    @Inject private val userRepository: UserRepository,
) : CommandParserService {

    override val botCommand = JoinOrderCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        return joinOrder(update, matchResult)
    }

    private fun joinOrder(update: Update, matchResult: MatchResult): SendMessage {
        val (_, orderName, _) = matchResult.destructured

        val chatId = MessageUtils.getChatId(update)
        val order = orderRepository.findOpenOrderWithNameForChat(chatId, orderName)

        return when {
            order == null -> OrderUtils.getOrderNotFoundMessage(update, orderName)
            order.status == OrderStatus.Close ->
                OrderUtils.getOperationNotAllowedWhenOrderIsClosedMessage(update, orderName)
            else -> createUserIfNotAlreadyInAnotherOrder(update, order, orderName)
        }
    }

    private fun createUserIfNotAlreadyInAnotherOrder(
        update: Update,
        order: OrderEntity,
        orderName: String
    ): SendMessage {
        val telegramUserId = MessageUtils.getTelegramUserId(update)
        val users = userRepository.findUsers(telegramUserId)

        return users.firstOrNull {
            OrderUtils.isClosedAndElapsed(it.order!!, botReopenOrderTimeout)
        }?.let {
            MessageUtils.createMessage(update, OrderMessages.orderCanBeReopenedError(it.order?.name!!))
        } ?: users.firstOrNull {
            it.order?.status == OrderStatus.Open
        }?.let {
            getUserAlreadyJoinedAnotherOrderMessage(update, orderName, it.order?.name!!)
        } ?: createUser(update, order, orderName)
    }

    private fun createUser(update: Update, order: OrderEntity, orderName: String): SendMessage {
        return UserEntity().apply {
            telegramUserId = MessageUtils.getTelegramUserId(update)
            this.order = order
        }.also {
            userRepository.persist(it)
        }.let {
            MessageUtils.createMessage(update, "Successfully joined order '$orderName'")
        }
    }

    private fun getUserAlreadyJoinedAnotherOrderMessage(
        update: Update, orderName: String, joinedOrderName: String
    ): SendMessage {
        return MessageUtils.createMessage(
            update,
            if (orderName == joinedOrderName) "Error: you have already joined '$orderName'"
            else "Error: you cannot join '$orderName' order, you must first leave '$joinedOrderName' order"
        )
    }
}
