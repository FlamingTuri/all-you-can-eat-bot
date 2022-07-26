package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.dto.MessageDto
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

        MessageDto.getMessageDto(update)?.also {
            try {
                handleUpdateAndNotifyResult(absSender, it)
            } catch (exception: Exception) {
                handleUnexpectedError(absSender, it, exception)
            }
        }
    }

    private fun handleUpdateAndNotifyResult(absSender: AbsSender, messageDto: MessageDto) {
        sendMessage(absSender, updateParserService.handleUpdate(messageDto))
    }

    private fun handleUnexpectedError(absSender: AbsSender, messageDto: MessageDto, exception: Exception) {
        Log.error(
            "unexpected error occurred: " +
                    "chatId ${MessageUtils.getChatId(messageDto)}, " +
                    "userId ${MessageUtils.getTelegramUserId(messageDto)}, " +
                    "message '${messageDto.text}' ",
            exception
        )
        val message = MessageUtils.createMessage(
            messageDto, "Error: something unexpected happened. Reason: ${exception.message}"
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
