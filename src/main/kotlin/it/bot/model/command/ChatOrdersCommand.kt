package it.bot.model.command

class ChatOrdersCommand : BotCommand {

    override val command: String = "/chatOrders"

    override val pattern: String = "(\\s*)"

    override val format: String = ""

    override val description: String = "list the orders in the chat"

    override val commandType: CommandType = CommandType.Group
}
