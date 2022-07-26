package it.bot.service.impl.command

import it.bot.model.command.NameDishCommand
import it.bot.model.dto.MessageDto
import it.bot.repository.DishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.UserUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NameDishService(
    private val userRepository: UserRepository,
    private val dishRepository: DishRepository
) : CommandParserService {

    override val botCommand = NameDishCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val (menuNumber, dishName) = destructure(matchResult)

        val user = userRepository.findUser(MessageUtils.getTelegramUserId(messageDto))
        return if (user == null) {
            UserUtils.getUserDoesNotBelongToOrderMessage(messageDto)
        } else {
            dishRepository.updateDishName(menuNumber, dishName, user.orderId!!)

            MessageUtils.createMessage(messageDto, "Successfully named dish $menuNumber to '$dishName'")
        }
    }

    private fun destructure(matchResult: MatchResult): Pair<Int, String> {
        val (_, _, menuNumber, _, dishName, _) = matchResult.destructured
        return Pair(menuNumber.toInt(), dishName)
    }
}