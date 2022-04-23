package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.Order
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import javax.enterprise.context.ApplicationScoped
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class CreateOrderService(private val orderRepository: OrderRepository) : CommandParserService {

    private val command = "/createOrder"

    override fun getCommand(): String = command

    override fun parseUpdate(update: Update): SendMessage {
        val regex = "$command (\\s*)(\\w+)(\\s*)".toRegex()
        return when (val matchResult = regex.matchEntire(update.message.text)) {
            null -> getInvalidOperationMessage(update)
            else -> createOrder(update, matchResult)
        }
    }

    private fun getInvalidOperationMessage(update: Update): SendMessage {
        Log.error("invalid format for operation $command: ${update.message.text}")
        return createMessage(
            update.message.chatId,
            "invalid format for operation $command, accepted format: $command {orderName}"
        )
    }

    private fun createOrder(update: Update, matchResult: MatchResult?): SendMessage {
        val (_, orderName, _) = matchResult!!.destructured
        Log.info("success $orderName")
        createOrder(update, orderName)
        return createMessage(update.message.chatId, "Successfully created order '$orderName'")
    }

    private fun createOrder(update: Update, orderName: String) {
        Log.info(update.message.chat.userName)
        Log.info(update.message.chat.id)

        val order = Order()
        order.name = orderName
        order.chatId = update.message.chatId
        order.status = OrderStatus.Open

        orderRepository.save(order)
    }

    private fun createMessage(chatId: Long, messageText: String): SendMessage {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = messageText
        return message
    }
}