package it.bot.util

import io.quarkus.logging.Log
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

object MessageUtils {

    fun getInvalidCommandMessage(update: Update, command: String, commandFormat: String): SendMessage {
        Log.error("invalid format for operation $command: ${update.message.text}")
        return createMessage(
            update,
            "Error: invalid format for operation $command, accepted format: $command $commandFormat"
        )
    }

    fun createMessage(update: Update, messageText: String, markdown: Boolean = false): SendMessage {
        return createMessage(getChatId(update), messageText, markdown)
    }

    fun createMessage(chatId: Long, messageText: String, markdown: Boolean = false): SendMessage {
        return SendMessage().apply {
            this.chatId = chatId.toString()
            text = messageText
            enableMarkdown(markdown)
        }
    }

    fun getTelegramUserId(update: Update): Long = update.message.from.id

    fun getChatId(update: Update): Long = update.message.chatId

    fun getChatMessage(update: Update): String = update.message.text

}
