package it.bot

import io.quarkus.logging.Log
import it.bot.service.CreateOrderService
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class AllYouCanEatBot(
    private val botToken: String,
    private val createOrderService: CreateOrderService
) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        println("update received")
        if (update.hasMessage() && update.message.hasText()) {
            val responseMessage = when {
                matches(createOrderService.getCommand(), update.message.text) -> createOrderService.parseUpdate(update)
                else -> getCommandNotSupportedMessage(update)
            }
            responseMessage?.let { sendMessage(it) }
        }
    }

    private fun matches(botCommand: String, text: String): Boolean {
        return text.startsWith("$botCommand ") or (text == botCommand)
    }

    private fun sendMessage(message: SendMessage) {
        try {
            execute(message)
        } catch (e: TelegramApiException) {
            Log.error(e)
        }
    }

    private fun getCommandNotSupportedMessage(update: Update): SendMessage {
        val errorMessage = "no command support for '${update.message.text}'"
        Log.error(errorMessage)

        val message = SendMessage()
        message.chatId = update.message.chatId.toString()
        message.text = errorMessage

        return message
    }

    override fun getBotUsername(): String {
        return "AllYouCanEatBot"
    }

    override fun getBotToken(): String {
        return botToken
    }
}
