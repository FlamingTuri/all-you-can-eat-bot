package it.bot.model.command

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

interface KeyboardButton {

    fun getInlineKeyboardButton(): InlineKeyboardButton {
        return InlineKeyboardButton().apply {
            text = getInlineKeyboardButtonText()
            callbackData = this@KeyboardButton.getCallbackData()
        }
    }

    fun getInlineKeyboardButtonText(): String

    fun getCallbackData(): String
}
