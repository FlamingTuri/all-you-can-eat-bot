package it.bot.service.impl.command

import it.bot.model.command.AddDishesCommand
import it.bot.model.command.BlameDishCommand
import it.bot.model.command.ChatOrdersCommand
import it.bot.model.command.CloseOrderCommand
import it.bot.model.command.CreateOrderCommand
import it.bot.model.command.JoinOrderCommand
import it.bot.model.command.KeyboardButton
import it.bot.model.command.LeaveOrderCommand
import it.bot.model.command.NameDishCommand
import it.bot.model.command.OpenOrderCommand
import it.bot.model.command.RemoveDishCommand
import it.bot.model.command.ShowButtonsCommand
import it.bot.model.command.ShowOrderCommand
import it.bot.model.dto.MessageDto
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShowButtonsService : CommandParserService {

    override val botCommand = ShowButtonsCommand()
    private val orderButtonsSubCommand = "/orderButtons"
    private val dishButtonsSubCommand = "/dishButtons"

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        val subCommand = destructure(matchResult)

        val response = MessageUtils.createMessage(messageDto, "Select the operation")

        return response.apply {
            replyMarkup = InlineKeyboardMarkup().apply {
                keyboard = getKeyboardButtons(subCommand)
            }
        }
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, _, _, subCommand, _) = matchResult.destructured
        return subCommand
    }

    private fun getKeyboardButtons(subCommand: String): List<List<InlineKeyboardButton>> {
        return when (subCommand) {
            orderButtonsSubCommand -> getOrderButtonsMenu()
            dishButtonsSubCommand -> getDishButtonsMenu()
            else -> getMainButtonsMenu()
        }.map { it.filterNotNull() }
    }

    private fun getOrderButtonsMenu(): List<List<InlineKeyboardButton?>> {
        return mutableListOf(
            mapToInlineKeyboardButtons(CreateOrderCommand(), OpenOrderCommand(), CloseOrderCommand()),
            mapToInlineKeyboardButtons(JoinOrderCommand(), LeaveOrderCommand()),
            mapToInlineKeyboardButtons(ChatOrdersCommand(), ShowOrderCommand())
        )
    }

    private fun getDishButtonsMenu(): List<List<InlineKeyboardButton?>> {
        return mutableListOf(
            mapToInlineKeyboardButtons(AddDishesCommand(), NameDishCommand(), RemoveDishCommand(), BlameDishCommand())
        )
    }

    private fun getMainButtonsMenu(): List<List<InlineKeyboardButton>> {
        return mutableListOf(
            mutableListOf(
                InlineKeyboardButton().apply {
                    text = "order"
                    callbackData = "${botCommand.command} $orderButtonsSubCommand"
                },
                InlineKeyboardButton().apply {
                    text = "dish"
                    callbackData = "${botCommand.command} $dishButtonsSubCommand"
                }
            )
        )
    }

    private fun mapToInlineKeyboardButtons(vararg keyboardButton: KeyboardButton): List<InlineKeyboardButton> {
        return keyboardButton.map { it.getInlineKeyboardButton() }
    }
}
