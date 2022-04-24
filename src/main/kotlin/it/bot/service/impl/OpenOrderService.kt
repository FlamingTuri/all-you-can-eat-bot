package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import java.util.Calendar
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update


@ApplicationScoped
class OpenOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    @Inject private val orderRepository: OrderRepository
) : CommandParserService() {

    override val command: String = "/openOrder"

    override val commandPattern: String = "(\\s*)(\\w+)(\\s*)"

    override val commandFormat: String = "{orderName}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val (_, orderName, _) = matchResult.destructured
        val order = orderRepository.findOrderWithNameForChat(MessageUtils.getChatId(update), orderName)

        return when {
            order == null -> getOrderNotFoundMessage(update, orderName)
            order.status == OrderStatus.Open -> getOrderAlreadyOpenMessage(update, order)
            else -> openOrderIfTimeoutIsNotElapsed(update, order)
        }
    }

    private fun getOrderNotFoundMessage(update: Update, orderName: String): SendMessage {
        Log.error("order $orderName not found for chatId ${MessageUtils.getChatId(update)}")
        return MessageUtils.createMessage(
            update,
            "Error: order '$orderName' not found closed order in the current chat"
        )
    }

    private fun getOrderAlreadyOpenMessage(update: Update, order: OrderEntity): SendMessage {
        Log.error("order ${order.orderId} is still open for chatId ${MessageUtils.getChatId(update)}")
        return MessageUtils.createMessage(
            update,
            "Error: order '${order.name}' is still open"
        )
    }

    private fun openOrderIfTimeoutIsNotElapsed(update: Update, order: OrderEntity): SendMessage {
        return if (isTimeElapsed(order)) {
            Log.error(
                "order has been closed for more than $botReopenOrderTimeout minutes: " +
                        "close time ${order.lastUpdateDate}, " +
                        "current time ${Calendar.getInstance().time}"
            )
            MessageUtils.createMessage(
                update,
                "Error: order '${order.name}' can not be reopened " +
                        "since it has been closed for more than $botReopenOrderTimeout minutes"
            )
        } else {
            openOrder(update, order)
        }
    }

    private fun isTimeElapsed(order: OrderEntity): Boolean {
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.MINUTE, -botReopenOrderTimeout)
        return currentTimeNow.time >= order.lastUpdateDate
    }

    private fun openOrder(update: Update, order: OrderEntity): SendMessage {
        order.status = OrderStatus.Open
        orderRepository.persist(order)
        return MessageUtils.createMessage(
            update,
            "Successfully opened order '${order.name}'"
        )
    }
}