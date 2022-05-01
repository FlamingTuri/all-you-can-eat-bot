package it.bot.model.command

class HelpMessageCommand : BotCommand {

    override val command: String = "/help"

    override val pattern: String = "(\\s*)"

    override val format: String = ""

    override val description: String = "display this help message"
}
