package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.UserEntity
import it.bot.repository.OrderRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class JoinOrderService(
    @Inject private val orderRepository: OrderRepository,
    @Inject private val userRepository: UserRepository
) : CommandParserService {

    private val command = "/joinOrder"

    override fun getCommand(): String = command

    @Transactional
    override fun parseUpdate(update: Update): SendMessage {
        val regex = "$command (\\s*)(\\w+)(\\s*)".toRegex()
        return when (val matchResult = regex.matchEntire(update.message.text)) {
            null -> MessageUtils.getInvalidCommandMessage(update, command)
            else -> joinOrder(update, matchResult)
        }
    }

    private fun joinOrder(update: Update, matchResult: MatchResult): SendMessage {
        val (_, orderName, _) = matchResult!!.destructured

        val chatId = MessageUtils.getChatId(update)
        val order = orderRepository.findOpenOrderWithNameForChat(chatId, orderName)

        if (order == null) {
            Log.error("order $orderName does not exists in chat $chatId")
            return MessageUtils.createMessage(
                update,
                "You can not join $orderName order, since it does not exists in the current chat"
            )
        }

        val userEntity = UserEntity().apply {
            telegramUserId = MessageUtils.getUserId(update)
            this.order = order
        }

        userRepository.persist(userEntity)

        return MessageUtils.createMessage(update, "Successfully joined order $orderName")
    }
}
