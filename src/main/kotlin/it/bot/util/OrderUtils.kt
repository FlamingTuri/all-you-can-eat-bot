package it.bot.util

import io.quarkus.logging.Log
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

object OrderUtils {

    fun getOrderNotFoundMessage(update: Update, orderName: String): SendMessage {
        Log.error("order $orderName not found for chatId ${MessageUtils.getChatId(update)}")
        return MessageUtils.createMessage(
            update,
            "Error: order '$orderName' not found for the current chat"
        )
    }
}