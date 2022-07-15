package it.bot.service.impl.command

import it.bot.model.command.JoinOrderCommand
import it.bot.model.dto.MessageDto
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
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JoinOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) : CommandParserService {

    override val botCommand = JoinOrderCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        return joinOrder(messageDto, matchResult)
    }

    private fun joinOrder(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val orderName = destructure(matchResult)

        val chatId = MessageUtils.getChatId(messageDto)
        val order = orderRepository.findOpenOrderWithNameForChat(chatId, orderName)

        return when {
            order == null -> OrderUtils.getOrderNotFoundMessage(messageDto, orderName)
            order.status == OrderStatus.Closed ->
                OrderUtils.getOperationNotAllowedWhenOrderIsClosedMessage(messageDto, orderName)
            else -> createUserIfNotAlreadyInAnotherOrder(messageDto, order, orderName)
        }
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, _, orderName, _) = matchResult.destructured
        return orderName
    }

    private fun createUserIfNotAlreadyInAnotherOrder(
        messageDto: MessageDto,
        order: OrderEntity,
        orderName: String
    ): SendMessage {
        val telegramUserId = MessageUtils.getTelegramUserId(messageDto)
        val users = userRepository.findUsers(telegramUserId)

        return users.firstOrNull {
            OrderUtils.isClosedAndElapsed(it.order!!, botReopenOrderTimeout)
        }?.let {
            MessageUtils.createMessage(messageDto, OrderMessages.orderCanBeReopenedError(it.order?.name!!))
        } ?: users.firstOrNull {
            it.order?.status == OrderStatus.Open
        }?.let {
            getUserAlreadyJoinedAnotherOrderMessage(messageDto, orderName, it.order?.name!!)
        } ?: createUser(messageDto, order, orderName)
    }

    private fun createUser(messageDto: MessageDto, order: OrderEntity, orderName: String): SendMessage {
        return UserEntity().apply {
            telegramUserId = MessageUtils.getTelegramUserId(messageDto)
            this.order = order
        }.also {
            userRepository.persist(it)
        }.let {
            MessageUtils.createMessage(messageDto, "Successfully joined order '$orderName'")
        }
    }

    private fun getUserAlreadyJoinedAnotherOrderMessage(
        messageDto: MessageDto, orderName: String, joinedOrderName: String
    ): SendMessage {
        return MessageUtils.createMessage(
            messageDto,
            if (orderName == joinedOrderName) "Error: you have already joined '$orderName'"
            else "Error: you cannot join '$orderName' order, you must first leave '$joinedOrderName' order"
        )
    }
}
