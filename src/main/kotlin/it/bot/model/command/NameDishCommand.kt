package it.bot.model.command

class NameDishCommand : BotCommand {

    override val command: String = "/nameDish"

    override val pattern: String = "(\\s+)(\\d+)(\\s+)(\\w+)(\\s*)"

    override val format: String = "{menuNumber} {dishName}"

    override val description: String = "set or change the name of a dish"
}
