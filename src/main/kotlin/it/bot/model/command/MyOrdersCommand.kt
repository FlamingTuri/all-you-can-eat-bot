package it.bot.model.command

class MyOrdersCommand : BotCommand {

    override val command: String = "/myOrders"

    override val pattern: String = "(\\s*)"

    override val format: String = ""

    override val description: String = "list your orders"

    override val commandType: CommandType = CommandType.Anywhere
}
