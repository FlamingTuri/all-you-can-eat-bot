package it.bot.model.command

import it.bot.util.Regexes

class LeaveOrderCommand : BotCommand, KeyboardButton {

    override val command = "/leaveOrder"

    override val pattern = "(\\s+)${Regexes.namePattern}(\\s*)"

    override val format: String = "{orderName}"

    override val description: String = "leave an OPEN order"

    override val commandType: CommandType = CommandType.Group

    override fun getInlineKeyboardButtonText(): String = "leave"

    override fun getCallbackData(): String = command
}
