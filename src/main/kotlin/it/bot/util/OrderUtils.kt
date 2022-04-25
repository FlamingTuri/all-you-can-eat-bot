package it.bot.util

import io.quarkus.logging.Log
import it.bot.model.dto.DishDto
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

    fun createOrderRecap(orderDishes: List<DishDto>): String {
        val maxMenuNumber = orderDishes.maxOf { it.menuNumber }
        val padding = "$maxMenuNumber".length

        return orderDishes.joinToString("\n") {
            val paddedMenuNumber = "${it.menuNumber}".padEnd(padding)
            "- number $paddedMenuNumber    x ${it.quantity}  ${FormatUtils.wrapIfNotNull(it.name)}"
        }
    }
}