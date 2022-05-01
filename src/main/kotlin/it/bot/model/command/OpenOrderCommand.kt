package it.bot.model.command

import it.bot.util.Regexes

class OpenOrderCommand : BotCommand {

    override val command: String = "/openOrder"

    override val pattern: String = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "open a CLOSED order"
}
