package it.bot.util

import io.quarkus.logging.Log
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

object MessageUtils {

    fun getInvalidCommandMessage(update: Update, command: String): SendMessage {
        Log.error("invalid format for operation $command: ${update.message.text}")
        return createMessage(
            update,
            "invalid format for operation $command, accepted format: $command {orderName}"
        )
    }

    fun createMessage(update: Update, messageText: String): SendMessage {
        return createMessage(getChatId(update), messageText)
    }

    fun createMessage(chatId: Long, messageText: String): SendMessage {
        return SendMessage().apply {
            this.chatId = chatId.toString()
            text = messageText
        }
    }

    fun getTelegramUserId(update: Update): Long = update.message.from.id

    fun getChatId(update: Update): Long = update.message.chatId

    fun getChatMessage(update: Update): String = update.message.text

}
