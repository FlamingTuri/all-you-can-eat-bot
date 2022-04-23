package it.bot

import io.quarkus.logging.Log
import it.bot.model.entity.Order
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class AllYouCanEatBot(
    private val botToken: String,
    private val orderRepository: OrderRepository
) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        println("update received")
        if (update.hasMessage() && update.message.hasText()) {
            when {
                matches("/createOrder", update.message.text) -> {//(\s*)
                    val regex = "/createOrder (\\s*)(\\w+)(\\s*)".toRegex()
                    when (val matchResult = regex.matchEntire(update.message.text)) {
                        null -> {
                            Log.error("invalid format for operation /createOrder: ${update.message.text}")
                            sendMessage(
                                update.message.chatId,
                                "invalid format for operation /createOrder, accepted format: /createOrder {orderName}"
                            )
                        }
                        else -> {
                            val (_, orderName, _) = matchResult!!.destructured
                            Log.info("success $orderName")
                            createOrder(update, orderName)
                        }
                    }
                }
                else -> sendErrorMessage(update)
            }
        }
    }

    private fun matches(operation: String, text: String): Boolean {
        return text.startsWith("$operation ")
    }

    private fun createOrder(update: Update, orderName: String) {
        Log.info(update.message.chat.userName)
        Log.info(update.message.chat.id)

        val order = Order()
        order.name = orderName
        order.chatId = update.message.chatId
        order.status = OrderStatus.Open
        orderRepository.save(order)

        sendMessage(update.message.chatId, "Successfully created order '$orderName'")
    }

    private fun sendMessage(chatId: Long, messageText: String) {
        try {
            val message = SendMessage()
            message.chatId = chatId.toString()
            message.text = messageText
            execute(message)
        } catch (e: TelegramApiException) {
            Log.error(e)
        }
    }

    private fun sendErrorMessage(update: Update) {
        val errorMessage = "no command support for '${update.message.text}'"
        Log.error(errorMessage)
        val message = SendMessage()
        message.chatId = update.message.chatId.toString()
        message.text = errorMessage
        execute(message)
    }

    override fun getBotUsername(): String {
        return "AllYouCanEatBot"
    }

    override fun getBotToken(): String {
        return botToken
    }
}
