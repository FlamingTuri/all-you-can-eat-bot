package it.bot.service.impl

import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class JoinOrderService(
    @Inject private val orderRepository: OrderRepository,
    @Inject private val userRepository: UserRepository,
) : CommandParserService() {

    override val command = "/joinOrder"

    override val commandPattern = "(\\s*)(\\w+)(\\s*)"

    override val commandFormat: String = "{orderName}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        return joinOrder(update, matchResult)
    }

    private fun joinOrder(update: Update, matchResult: MatchResult): SendMessage {
        val (_, orderName, _) = matchResult.destructured

        val chatId = MessageUtils.getChatId(update)
        val order = orderRepository.findOpenOrderWithNameForChat(chatId, orderName)

        return when {
            order == null -> OrderUtils.getOrderNotFoundMessage(update, orderName)
            order?.status == OrderStatus.Close ->
                OrderUtils.getOperationNotAllowedWhenOrderIsClosedMessage(update, orderName)
            else -> {
                val userEntity = UserEntity().apply {
                    telegramUserId = MessageUtils.getTelegramUserId(update)
                    this.order = order
                }

                userRepository.persist(userEntity)

                MessageUtils.createMessage(update, "Successfully joined order $orderName")
            }
        }
    }
}
