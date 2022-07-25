package it.bot.service.impl.command

import it.bot.model.command.AddDishesCommand
import it.bot.model.command.BlameDishCommand
import it.bot.model.command.NameDishCommand
import it.bot.model.command.RemoveDishCommand
import it.bot.model.command.ShowButtonsCommand
import it.bot.model.dto.MessageDto
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShowButtonsService : CommandParserService {

    override val botCommand = ShowButtonsCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        val response = MessageUtils.createMessage(messageDto, "Select the operation")
        return response.apply {
            replyMarkup = InlineKeyboardMarkup().apply {
                keyboard = mutableListOf(
                    mutableListOf(
                        AddDishesCommand().getInlineKeyboardButton(),
                        NameDishCommand().getInlineKeyboardButton(),
                        RemoveDishCommand().getInlineKeyboardButton(),
                        BlameDishCommand().getInlineKeyboardButton()
                    )
                )
            }
        }
    }
}