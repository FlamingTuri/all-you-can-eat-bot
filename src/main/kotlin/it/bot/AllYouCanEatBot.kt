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
            commandParserServices.find {
                matches(it.getCommand(), MessageUtils.getChatMessage(update))
            }.let {
                parseUpdate(it, update)
            }.also {
                sendMessage(it)
            }
        }
    }

    private fun matches(botCommand: String, text: String): Boolean {
        return text.startsWith("$botCommand ") or (text == botCommand)
    }

    private fun parseUpdate(commandParserService: CommandParserService?, update: Update): SendMessage? {
        return if (commandParserService == null) {
            getCommandNotSupportedMessage(update)
        } else {
            commandParserService.parseUpdate(update)
        }
    }

    private fun sendMessage(message: SendMessage?) {
        if (message == null) {
            Log.info("no message to send")
        } else {
            try {
                execute(message)
            } catch (e: TelegramApiException) {
                Log.error(e)
            }
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
