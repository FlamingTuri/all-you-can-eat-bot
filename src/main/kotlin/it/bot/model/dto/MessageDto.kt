package it.bot.model.dto

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class MessageDto(val messageId: Int, val from: User, var text: String, val chat: Chat) {

    companion object {
        fun getMessageDto(update: Update): MessageDto? {
            return when {
                update.hasMessage() && update.message.hasText() -> MessageDto(
                    update.message.messageId,
                    update.message.from,
                    update.message.text,
                    update.message.chat
                )
                update.hasCallbackQuery() -> MessageDto(
                    update.callbackQuery.message.messageId,
                    update.callbackQuery.from,
                    update.callbackQuery.data,
                    update.callbackQuery.message.chat
                )
                else -> null
            }
        }
    }
}
