package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class CloseOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    @Inject private val orderRepository: OrderRepository
) : CommandParserService() {

    override val command: String = "/closeOrder"

    override val commandPattern: String = "(\\s*)(\\w+)(\\s*)"

    override val commandFormat: String = "{orderName}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val (_, orderName, _) = matchResult.destructured
        val order = orderRepository.findOrderWithNameForChat(MessageUtils.getChatId(update), orderName)

        return when {
            order == null -> getOrderNotFoundMessage(update, orderName)
            order.status == OrderStatus.Close -> getOrderAlreadyClosedMessage(update, order)
            else -> closeOrder(update, order)
        }
    }

    private fun getOrderNotFoundMessage(update: Update, orderName: String): SendMessage {
        Log.error("order $orderName not found for chatId ${MessageUtils.getChatId(update)}")
        return MessageUtils.createMessage(
            update,
            "Error: order '$orderName' not found closed order in the current chat"
        )
    }

    private fun getOrderAlreadyClosedMessage(update: Update, order: OrderEntity): SendMessage {
        Log.error("order ${order.orderId} has been already closed for chatId ${MessageUtils.getChatId(update)}")
        return MessageUtils.createMessage(
            update,
            "Error: order '${order.name}' has been already closed"
        )
    }

    private fun closeOrder(update: Update, order: OrderEntity): SendMessage {
        order.status = OrderStatus.Close
        orderRepository.persist(order)
        return MessageUtils.createMessage(
            update,
            "Successfully closed order '${order.name}'. " +
                    "If you closed it by accident, you have $botReopenOrderTimeout minutes to open it back."
        )
    }
}
