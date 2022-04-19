package it.bot

import io.quarkus.logging.Log
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class AllYouCanEatBot(private val botToken: String) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        println("update received")
        if (update.hasMessage() && update.message.hasText()) {
            val messageText = update.message.text
            val chatId = update.message.chatId.toString()
            val message = SendMessage()
            message.chatId = chatId
            message.text = messageText
            try {
                execute(message)
            } catch (e: TelegramApiException) {
                Log.error(e)
            }
        }
    }

    override fun getBotUsername(): String {
        return "AllYouCanEatBot"
    }

    override fun getBotToken(): String {
        return botToken
    }
}
