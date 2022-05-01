package it.bot.model.command

import it.bot.util.Regexes

class ShowOrderCommand : BotCommand {

    override val command: String = "/showOrder"

    override val pattern: String = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "display a recap of an order"
}
