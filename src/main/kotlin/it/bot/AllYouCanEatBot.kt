package it.bot

import io.quarkus.logging.Log
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class AllYouCanEatBot(
    private val botToken: String,
    private val commandParserServices: List<CommandParserService>,
) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        if (Log.isDebugEnabled()) {
            Log.debug("update received $update")
        }

        if (update.hasMessage() && update.message.hasText()) {
            val commandParserService = commandParserServices.find {
                matches(it.getCommand(), MessageUtils.getChatMessage(update))
            }

            val responseMessage = commandParserService?.parseUpdate(update) ?: getCommandNotSupportedMessage(update)

            responseMessage.let { sendMessage(it) }
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
        val errorMessage = "no command support for '${MessageUtils.getChatMessage(update)}'"
        Log.error(errorMessage)
        return MessageUtils.createMessage(update, errorMessage)
    }

    override fun getBotUsername(): String {
        return "AllYouCanEatBot"
    }

    override fun getBotToken(): String {
        return botToken
    }
}
