package it.bot.util

import io.quarkus.logging.Log
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

object UserUtils {

    fun getUserDoesNotBelongToOrderMessage(update: Update): SendMessage {
        Log.error("user ${MessageUtils.getTelegramUserId(update)} not found")
        return MessageUtils.createMessage(
            update,
            "Error: you are not part of an order. Use /joinOrder command before adding a dish"
        )
    }
}