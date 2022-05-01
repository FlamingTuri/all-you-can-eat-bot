package it.bot.model.command

import it.bot.util.Regexes

class LeaveOrderCommand : BotCommand {

    override val command = "/leaveOrder"

    override val pattern = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "leaves an OPEN order"
}
