package it.bot.model.command

import it.bot.util.Regexes

class CloseOrderCommand : BotCommand {

    override val command: String = "/closeOrder"

    override val pattern: String = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "close an order, preventing further modifications"
}
