package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.Order
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class CreateOrderService(@Inject private val orderRepository: OrderRepository) : CommandParserService {

    private val command = "/createOrder"

    override fun getCommand(): String = command

    @Transactional
    override fun parseUpdate(update: Update): SendMessage {
        val regex = "$command (\\s*)(\\w+)(\\s*)".toRegex()
        return when (val matchResult = regex.matchEntire(update.message.text)) {
            null -> getInvalidOperationMessage(update)
            else -> createOrderIfNotExists(update, matchResult)
        }
    }

    private fun getInvalidOperationMessage(update: Update): SendMessage {
        Log.error("invalid format for operation $command: ${update.message.text}")
        return createMessage(
            update.message.chatId,
            "invalid format for operation $command, accepted format: $command {orderName}"
        )
    }

    private fun createOrderIfNotExists(update: Update, matchResult: MatchResult?): SendMessage {
        val (_, orderName, _) = matchResult!!.destructured

        if (checkIfOrderAlreadyExists(update, orderName)) {
            return createMessage(update, "Error: an order with the same name already exists for current chat")
        }

        createOrder(update, orderName)
        return createMessage(update, "Successfully created order '$orderName'")
    }

    private fun checkIfOrderAlreadyExists(update: Update, orderName: String): Boolean {
        val order = Order()
        order.chatId = update.message.chatId
        order.name = orderName
        return orderRepository.existsOrderWithNameForChat(update.message.chatId, orderName)
    }

    private fun createOrder(update: Update, orderName: String) {
        val chatId = update.message.chatId
        Log.info("creating order with name $orderName for chat $chatId")

        val order = Order()
        order.name = orderName
        order.chatId = chatId
        order.status = OrderStatus.Open

        orderRepository.persist(order)
    }

    private fun createMessage(update: Update, messageText: String): SendMessage {
        return createMessage(update.message.chatId, messageText)
    }

    private fun createMessage(chatId: Long, messageText: String): SendMessage {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = messageText
        return message
    }
}
