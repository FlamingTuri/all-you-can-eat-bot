package it.bot.service.impl

import it.bot.model.command.NameDishCommand
import it.bot.repository.DishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.UserUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class NameDishService(
    @Inject private val userRepository: UserRepository,
    @Inject private val dishRepository: DishRepository
) : CommandParserService() {

    override val botCommand = NameDishCommand()

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val (menuNumber, dishName) = destructure(matchResult)

        val user = userRepository.findUser(MessageUtils.getTelegramUserId(update))
        return if (user == null) {
            UserUtils.getUserDoesNotBelongToOrderMessage(update)
        } else {
            dishRepository.updateDishName(menuNumber, dishName, user.orderId!!)

            MessageUtils.createMessage(update, "Successfully named dish $menuNumber to '$dishName'")
        }
    }

    private fun destructure(matchResult: MatchResult): Pair<Int, String> {
        val (_, menuNumber, _, dishName, _) = matchResult.destructured
        return Pair(menuNumber.toInt(), dishName)
    }
}