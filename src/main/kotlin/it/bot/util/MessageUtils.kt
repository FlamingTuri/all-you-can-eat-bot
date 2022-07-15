package it.bot.util

import io.quarkus.logging.Log
import it.bot.model.dto.MessageDto
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object MessageUtils {

    fun getInvalidCommandMessage(messageDto: MessageDto, command: String, commandFormat: String): SendMessage {
        Log.error("invalid format for operation $command: ${messageDto.text}")
        return createMessage(
            messageDto,
            "Error: invalid format for operation $command, accepted format: $command $commandFormat"
        )
    }

    fun createMessage(messageDto: MessageDto, messageText: String): SendMessage {
        return createMessage(getChatId(messageDto), messageText)
    }

    fun createMessage(chatId: Long, messageText: String): SendMessage {
        return SendMessage().apply {
            this.chatId = chatId.toString()
            text = messageText
        }
    }

    fun getTelegramUserId(messageDto: MessageDto): Long = messageDto.from.id

    fun getChatId(messageDto: MessageDto): Long = messageDto.chat.id

}
