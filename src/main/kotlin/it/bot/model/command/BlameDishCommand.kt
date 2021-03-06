package it.bot.model.command

import it.bot.util.Regexes

class BlameDishCommand : BotCommand, KeyboardButton  {

    override val command: String = "/blame"

    override val pattern: String = "(\\s+)(\\d+)${Regexes.namePattern}?(\\s*)"

    override val format: String = "{menuNumber} {orderName:}"

    override val description: String = "search who ordered a dish"

    override val commandType: CommandType = CommandType.Group

    override fun getInlineKeyboardButtonText(): String = "blame"

    override fun getCallbackData(): String = command
}
