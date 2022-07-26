package it.bot.model.command

class ShowDishButtonsCommand : BotCommand {

    override val command: String = "/showDishButtons"

    override val pattern: String = "\\s*"

    override val format: String = ""

    override val description: String = "show dish buttons menu"

    override val commandType: CommandType = CommandType.Anywhere
}
