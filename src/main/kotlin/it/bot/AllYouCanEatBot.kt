package it.bot

import io.quarkus.logging.Log
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class AllYouCanEatBot(private val botToken: String) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        println("update received")
        if (update.hasMessage() && update.message.hasText()) {
            when (update.message.text) {
                "/createOrder" -> {}//TODO
                else -> sendErrorMessage(update)
            }
        }
    }

    private fun sendErrorMessage(update: Update) {
        val errorMessage = "no command support for '${update.message.text}'"
        Log.error(errorMessage)
        val message = SendMessage()
        message.chatId = update.message.chatId.toString()
        message.text = errorMessage
        execute(message)
    }

    override fun getBotUsername(): String {
        return "AllYouCanEatBot"
    }

    override fun getBotToken(): String {
        return botToken
    }
}
