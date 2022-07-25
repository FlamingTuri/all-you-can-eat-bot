package it.bot.model.command

import it.bot.util.Regexes

class NameDishCommand : BotCommand {

    override val command: String = "/nameDish"

    override val pattern: String = "(\\s+)(\\d+)(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{menuNumber} {dishName}"

    override val description: String = "set or change the name of a dish"

    override val commandType: CommandType = CommandType.Anywhere

    override fun getInlineKeyboardButtonText(): String? = "rename"
}
