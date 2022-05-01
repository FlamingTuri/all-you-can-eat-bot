package it.bot.model.command

import it.bot.util.Regexes

class AddDishCommand : BotCommand {

    override val command: String = "/addDish"

    override val pattern: String = "(\\s+)(\\d+)((\\s+)(\\d+))?((\\s+)${Regexes.namePattern})?(\\s*)"

    override val format: String = "{menuNumber} {quantity:1} {dishName:}"

    override val description: String = "add a dish to your order"
}
