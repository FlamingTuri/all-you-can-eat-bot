package it.bot.model.command

class RemoveDishCommand : BotCommand {

    override val command: String = "/removeDish"

    override val pattern: String = "(\\s+)(\\d+)((\\s+)(\\d+))?\\s*"

    override val format: String = "{menuNumber} {quantityToRemove:all}"

    override val description: String = "remove a dish from an OPEN order"
}
