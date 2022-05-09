package it.bot.service.impl.command

import it.bot.model.command.LeaveOrderCommand
import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.TimeUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class LeaveOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    @Inject private val orderRepository: OrderRepository,
    @Inject private val userRepository: UserRepository,
    @Inject private val userDishRepository: UserDishRepository
) : CommandParserService {

    override val botCommand = LeaveOrderCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val (_, orderName, _) = matchResult.destructured

        val telegramUserId = MessageUtils.getTelegramUserId(update)
        val chatId = MessageUtils.getChatId(update)
        val user = userRepository.findUser(telegramUserId, chatId, orderName)

        val messageText = when {
            user == null -> getUserForOrderNotFoundErrorMessage(orderName)
            OrderStatus.Closed == user.order?.status -> getLeaveClosedOrderErrorMessage(user)
            else -> leaveOpenOrderIfNoUserDishesArePresent(user)
        }

        return MessageUtils.createMessage(update, messageText)
    }

    private fun getUserForOrderNotFoundErrorMessage(orderName: String): String {
        return "Error: cannot leave order '$orderName' since you are not part of it"
    }

    private fun getLeaveClosedOrderErrorMessage(user: UserEntity): String {
        val timeElapsed = TimeUtils.hasTimeElapsed(user.order!!, botReopenOrderTimeout)
        return if (timeElapsed) {
            "Error: you cannot leave a closed order, but you can join a new one"
        } else {
            "Error: you cannot leave a closed order that could be reopened"
        }
    }

    private fun leaveOpenOrderIfNoUserDishesArePresent(user: UserEntity): String {
        val orderHasDishes = userDishRepository.checkIfOrderHasUserDishes(user.orderId!!, user.userId!!)
        return if (orderHasDishes) {
            "Error: cannot leave order '${user.order?.name}' since you have dishes on it. First remove them with /removeDish command."
        } else {
            userRepository.delete(user)
            "Successfully left order '${user.order?.name}'"
        }
    }
}
