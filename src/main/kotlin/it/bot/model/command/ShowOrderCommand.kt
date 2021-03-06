package it.bot.model.command

import it.bot.util.Regexes

class ShowOrderCommand : BotCommand, KeyboardButton {

    override val command: String = "/showOrder"

    override val pattern: String = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "display a recap of an order"

    override val commandType: CommandType = CommandType.Group

    override fun getInlineKeyboardButtonText(): String = "your order"

    override fun getCallbackData(): String = command
}
