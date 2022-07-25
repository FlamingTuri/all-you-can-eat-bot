package it.bot.model.command

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

interface BotCommand {

    val command: String

    val pattern: String

    val format: String

    val description: String

    val commandType: CommandType

    fun getInlineKeyboardButton(): InlineKeyboardButton? {
        return getInlineKeyboardButtonText()?.let {
            InlineKeyboardButton().apply {
                text = it
                callbackData = command
            }
        }
    }

    fun getInlineKeyboardButtonText(): String? = null

    fun matches(text: String, botUsername: String): Boolean {
        return text.startsWith("$command ", true) or
                text.startsWith("$command@$botUsername ", true) or
                isExactMatch(text, botUsername)
    }

    fun isExactMatch(text: String, botUsername: String): Boolean {
        return text.equals(command, true) or
                text.equals("$command@$botUsername", true)
    }
}
