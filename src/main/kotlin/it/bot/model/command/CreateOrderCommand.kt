package it.bot.model.command

import it.bot.util.Regexes

class CreateOrderCommand : BotCommand {

    override val command: String = "/createOrder"

    override val pattern: String = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "create a new order"

    override val commandType: CommandType = CommandType.Group
}
