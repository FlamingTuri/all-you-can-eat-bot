package it.bot.util

import io.quarkus.logging.Log
import it.bot.model.dto.DishDto
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.model.messages.OrderMessages
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object OrderUtils {

    fun getOrderNotFoundMessage(messageDto: MessageDto, orderName: String): SendMessage {
        Log.error("order $orderName not found for chatId ${MessageUtils.getChatId(messageDto)}")
        return MessageUtils.createMessage(messageDto, OrderMessages.orderNotFoundError(orderName))
    }

    fun getOperationNotAllowedWhenOrderIsClosedMessage(messageDto: MessageDto, orderName: String): SendMessage {
        Log.error("order $orderName is closed for chatId ${MessageUtils.getChatId(messageDto)}")
        return MessageUtils.createMessage(
            messageDto, OrderMessages.operationNotAllowedForClosedOrderError(orderName)
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

    fun isClosedAndElapsed(order: OrderEntity, minutes: Int): Boolean {
        return order.status == OrderStatus.Closed && !TimeUtils.hasTimeElapsed(order, minutes)
    }
}