package it.bot.service.impl.command

import it.bot.model.command.LeaveOrderCommand
import it.bot.model.dto.MessageDto
import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.TimeUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class LeaveOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    private val userRepository: UserRepository,
    private val userDishRepository: UserDishRepository
) : CommandParserService {

    override val botCommand = LeaveOrderCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        val orderName = destructure(matchResult)

        val telegramUserId = MessageUtils.getTelegramUserId(messageDto)
        val chatId = MessageUtils.getChatId(messageDto)
        val user = userRepository.findUser(telegramUserId, chatId, orderName)

        val messageText = when {
            user == null -> getUserForOrderNotFoundErrorMessage(orderName)
            OrderStatus.Closed == user.order?.status -> getLeaveClosedOrderErrorMessage(user)
            else -> leaveOpenOrderIfNoUserDishesArePresent(user)
        }

        return MessageUtils.createMessage(messageDto, messageText)
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, _, orderName, _) = matchResult.destructured
        return orderName
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
