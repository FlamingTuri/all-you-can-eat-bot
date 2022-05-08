package it.bot.model.command

class TestCommand : BotCommand {

    override val command: String = "/asd"

    override val pattern: String = "(\\s*)"

    override val format: String = ""

    override val description: String = "display the welcome message"

    override val commandType: CommandType = CommandType.Info
}