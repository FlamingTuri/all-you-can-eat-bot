package it.bot.service.impl.command

import it.bot.model.command.AddDishesCommand
import it.bot.model.command.BlameDishCommand
import it.bot.model.command.NameDishCommand
import it.bot.model.command.RemoveDishCommand
import it.bot.model.command.ShowDishButtonsCommand
import it.bot.model.dto.MessageDto
import it.bot.service.interfaces.CommandParserService
import it.bot.util.ButtonUtils
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShowDishButtonsService : CommandParserService {

    override val botCommand = ShowDishButtonsCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        val response = MessageUtils.createMessage(messageDto, "Dish operations menu:")

        return response.apply {
            replyMarkup = InlineKeyboardMarkup().apply {
                keyboard = getDishButtonsMenu()
            }
        }
    }

    private fun getDishButtonsMenu(): List<List<InlineKeyboardButton?>> {
        return mutableListOf(
            ButtonUtils.mapToInlineKeyboardButtons(
                AddDishesCommand(),
                NameDishCommand(),
                RemoveDishCommand(),
                BlameDishCommand()
            )
        )
    }
}