package it.bot.util

import io.quarkus.logging.Log
import it.bot.model.dto.MessageDto
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object UserUtils {

    fun getUserDoesNotBelongToOrderMessage(messageDto: MessageDto): SendMessage {
        Log.error("user ${MessageUtils.getTelegramUserId(messageDto)} not found")
        return MessageUtils.createMessage(
            messageDto,
            "Error: you are not part of an order. Use /joinOrder command before adding a dish"
        )
    }
}