package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class UpdateHandler(private val updateParserService: UpdateParserService) {

    fun handleUpdate(absSender: AbsSender, update: Update) {
        if (Log.isDebugEnabled()) {
            Log.debug("update received $update")
        }

        if (update.hasMessage() && update.message.hasText()) {
            try {
                handleUpdateAndNotifyResult(absSender, update)
            } catch (exception: Exception) {
                handleUnexpectedError(absSender, update, exception)
            }
        }
    }

    private fun handleUpdateAndNotifyResult(absSender: AbsSender, update: Update) {
        sendMessage(absSender, updateParserService.handleUpdate(update))
    }

    private fun handleUnexpectedError(absSender: AbsSender, update: Update, exception: Exception) {
        Log.error(
            "unexpected error occurred: " +
                    "chatId ${MessageUtils.getChatId(update)}, " +
                    "userId ${MessageUtils.getTelegramUserId(update)}, " +
                    "message '${MessageUtils.getChatMessage(update)}' ",
            exception
        )
        val message = MessageUtils.createMessage(
            update, "Error: something unexpected happened. Reason: ${exception.message}"
        )
        sendMessage(absSender, message)
    }

    private fun sendMessage(absSender: AbsSender, message: SendMessage?) {
        message?.also {
            try {
                absSender.execute(it)
            } catch (e: TelegramApiException) {
                Log.error(e)
            }
        } ?: Log.info("no message to send")
    }
}
