package it.bot.model.command

import it.bot.util.Regexes

class MyOrdersCommand : BotCommand {

    override val command: String = "/myOrders"

    override val pattern: String = "(\\s+${Regexes.namePattern})?(\\s*)"

    override val format: String = "{orderName:all}"

    override val description: String = "list your orders"

    override val commandType: CommandType = CommandType.Anywhere
}
