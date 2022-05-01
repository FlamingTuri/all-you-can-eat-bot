package it.bot.model.command

class CreateOrderCommand : BotCommand {

    override val command: String = "/createOrder"

    override val pattern: String = "(\\s+)(\\w+)(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "create a new order"
}
