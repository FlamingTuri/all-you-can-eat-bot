package it.bot.service.impl.command

import it.bot.model.command.ChatOrdersCommand
import it.bot.model.command.CloseOrderCommand
import it.bot.model.command.CreateOrderCommand
import it.bot.model.command.JoinOrderCommand
import it.bot.model.command.LeaveOrderCommand
import it.bot.model.command.OpenOrderCommand
import it.bot.model.command.ShowOrderButtonsCommand
import it.bot.model.command.ShowOrderCommand
import it.bot.model.dto.MessageDto
import it.bot.service.interfaces.CommandParserService
import it.bot.util.ButtonUtils
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShowOrderButtonsService : CommandParserService {

    override val botCommand = ShowOrderButtonsCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        val response = MessageUtils.createMessage(messageDto, "Select the operation")

        return response.apply {
            replyMarkup = InlineKeyboardMarkup().apply {
                keyboard = getOrderButtonsMenu()
            }
        }
    }

    private fun getOrderButtonsMenu(): List<List<InlineKeyboardButton?>> {
        return mutableListOf(
            ButtonUtils.mapToInlineKeyboardButtons(CreateOrderCommand(), OpenOrderCommand(), CloseOrderCommand()),
            ButtonUtils.mapToInlineKeyboardButtons(JoinOrderCommand(), LeaveOrderCommand()),
            ButtonUtils.mapToInlineKeyboardButtons(ChatOrdersCommand(), ShowOrderCommand())
        )
    }
}