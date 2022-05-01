package it.bot.model.command

class LeaveOrderCommand : BotCommand {

    override val command = "/leaveOrder"

    override val pattern = "(\\s+)(\\w+)(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "leaves an OPEN order"
}
