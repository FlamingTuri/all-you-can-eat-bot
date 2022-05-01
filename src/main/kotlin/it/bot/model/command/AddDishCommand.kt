package it.bot.model.command

class AddDishCommand : BotCommand {

    override val command: String = "/addDish"

    override val pattern: String = "(\\s+)(\\d+)((\\s+)(\\d+))?((\\s+)(\\w+))?(\\s*)"

    override val format: String = "{menuNumber} {quantity:1} {dishName:}"

    override val description: String = "add a dish to your order"
}
