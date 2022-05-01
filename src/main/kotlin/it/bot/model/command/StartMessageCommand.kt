package it.bot.model.command

class StartMessageCommand : BotCommand {

    override val command: String = "/start"

    override val pattern: String = "(\\s*)"

    override val format: String = ""

    override val description: String = "display the welcome message"

    override val commandType: CommandType = CommandType.Info
}