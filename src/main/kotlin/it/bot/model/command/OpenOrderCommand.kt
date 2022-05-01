package it.bot.model.command

class OpenOrderCommand : BotCommand {

    override val command: String = "/openOrder"

    override val pattern: String = "(\\s+)(\\w+)(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = ""
}
