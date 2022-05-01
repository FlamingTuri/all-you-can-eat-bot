package it.bot.model.command

class JoinOrderCommand : BotCommand {

    override val command = "/joinOrder"

    override val pattern = "(\\s+)(\\w+)(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "join an existing OPEN order"
}
