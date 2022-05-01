package it.bot.model.command

class BlameDishCommand : BotCommand {

    override val command: String = "/blame"

    override val pattern: String = "(\\s+)(\\d+)((\\s+)(\\w+))?(\\s*)"

    override val format: String = "{menuNumber} {orderName:}"

    override val description: String = ""
}
