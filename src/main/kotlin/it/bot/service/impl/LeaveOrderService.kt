package it.bot.service.impl

import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class LeaveOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    @Inject private val orderRepository: OrderRepository,
    @Inject private val userRepository: UserRepository,
    @Inject private val userDishRepository: UserDishRepository
) : CommandParserService() {

    override val command = "/leaveOrder"

    override val commandPattern = "(\\s*)(\\w+)(\\s*)"

    override val commandFormat: String = "{orderName}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val (_, orderName, _) = matchResult.destructured

        val telegramUserId = MessageUtils.getTelegramUserId(update)
        val chatId = MessageUtils.getChatId(update)
        val user = userRepository.findUser(telegramUserId, chatId, orderName)

        val messageText = when {
            user == null -> "Error: can not leave order '$orderName' since you are not part of it"
            OrderStatus.Open == user.order?.status -> leaveOpenOrderIfNoUserDishesArePresent(user)
            else -> "TODO"
        }

        return MessageUtils.createMessage(update, messageText)
    }

    private fun leaveOpenOrderIfNoUserDishesArePresent(user: UserEntity): String {
        val orderHasDishes = userDishRepository.checkIfOrderHasUserDishes(user.orderId!!, user.userId!!)
        return if (orderHasDishes) {
            "Error: can not leave order '${user.order?.name}' since you have dishes on it. First remove them with /removeDish command."
        } else {
            userRepository.delete(user)
            "Successfully left order '${user.order?.name}'"
        }
    }
}
