package it.bot.model.command

class ShowOrderButtonsCommand : BotCommand {

    override val command: String = "/showOrderButtons"

    override val pattern: String = "\\s*"

    override val format: String = ""

    override val description: String = "show order buttons menu"

    override val commandType: CommandType = CommandType.Anywhere
}
