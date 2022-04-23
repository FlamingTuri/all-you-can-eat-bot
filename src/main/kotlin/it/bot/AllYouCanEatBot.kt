package it.bot

import io.quarkus.logging.Log
import it.bot.service.impl.CreateOrderService
import it.bot.service.impl.JoinOrderService
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class AllYouCanEatBot(
    private val botToken: String,
    private val createOrderService: CreateOrderService,
    private val joinOrderService: JoinOrderService
) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        Log.debug("update received $update")
        Log.info(update.message.from.id)

        if (update.hasMessage() && update.message.hasText()) {
            val responseMessage = when {
                matches(createOrderService.getCommand(), update.message.text) -> createOrderService.parseUpdate(update)
                matches(joinOrderService.getCommand(), update.message.text) -> joinOrderService.parseUpdate(update)
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
