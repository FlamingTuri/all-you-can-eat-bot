package it.bot.util

import it.bot.model.command.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

object ButtonUtils {

    fun mapToInlineKeyboardButtons(vararg keyboardButton: KeyboardButton): List<InlineKeyboardButton> {
        return keyboardButton.map { it.getInlineKeyboardButton() }
    }
}