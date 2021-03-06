package it.bot.model.command

import it.bot.util.Regexes

class JoinOrderCommand : BotCommand, KeyboardButton {

    override val command = "/joinOrder"

    override val pattern = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "join an existing OPEN order"

    override val commandType: CommandType = CommandType.Group

    override fun getInlineKeyboardButtonText(): String = "join"

    override fun getCallbackData(): String = command
}
