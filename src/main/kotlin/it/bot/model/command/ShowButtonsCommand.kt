package it.bot.model.command

class ShowButtonsCommand : BotCommand {

    override val command: String = "/showButtons"

    override val pattern: String = "((\\s+)(\\/\\w+))?(\\s*)"

    override val format: String = "{subCommand}"

    override val description: String = "add a dish to your order"

    override val commandType: CommandType = CommandType.Anywhere
}