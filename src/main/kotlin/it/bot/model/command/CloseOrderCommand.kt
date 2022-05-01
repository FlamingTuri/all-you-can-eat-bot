package it.bot.model.command

class CloseOrderCommand : BotCommand {

    override val command: String = "/closeOrder"

    override val pattern: String = "(\\s+)(\\w+)(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "close an order, preventing further modifications"
}
