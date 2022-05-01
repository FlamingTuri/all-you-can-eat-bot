package it.bot.model.command

class ShowOrderCommand : BotCommand {

    override val command: String = "/showOrder"

    override val pattern: String = "(\\s+)(\\w+)(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = ""
}
