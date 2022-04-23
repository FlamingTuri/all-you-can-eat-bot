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
        return createMessage(update.message.chatId, messageText)
    }

    fun createMessage(chatId: Long, messageText: String): SendMessage {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = messageText
        return message
    }

}
