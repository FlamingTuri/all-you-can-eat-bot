package it.bot.service.impl.command

import io.quarkus.logging.Log
import it.bot.model.command.OpenOrderCommand
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.Calendar
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class OpenOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    private val orderRepository: OrderRepository
) : CommandParserService {

    override val botCommand = OpenOrderCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val orderName = destructure(matchResult)
        val order = orderRepository.findOrderWithNameForChat(MessageUtils.getChatId(messageDto), orderName)

        return when {
            order == null -> OrderUtils.getOrderNotFoundMessage(messageDto, orderName)
            order.status == OrderStatus.Open -> getOrderAlreadyOpenMessage(messageDto, order)
            else -> openOrderIfTimeoutIsNotElapsed(messageDto, order)
        }
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, _, orderName, _) = matchResult.destructured
        return orderName
    }

    private fun getOrderAlreadyOpenMessage(messageDto: MessageDto, order: OrderEntity): SendMessage {
        Log.error("order ${order.orderId} is still open for chatId ${MessageUtils.getChatId(messageDto)}")
        return MessageUtils.createMessage(
            messageDto,
            "Error: order '${order.name}' is still open"
        )
    }

    private fun openOrderIfTimeoutIsNotElapsed(messageDto: MessageDto, order: OrderEntity): SendMessage {
        return if (isTimeElapsed(order)) {
            Log.error(
                "order has been closed for more than $botReopenOrderTimeout minutes: " +
                        "close time ${order.lastUpdateDate}, " +
                        "current time ${Calendar.getInstance().time}"
            )
            MessageUtils.createMessage(
                messageDto,
                "Error: order '${order.name}' cannot be reopened " +
                        "since it has been closed for more than $botReopenOrderTimeout minutes"
            )
        } else {
            openOrder(messageDto, order)
        }
    }

    private fun isTimeElapsed(order: OrderEntity): Boolean {
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.MINUTE, -botReopenOrderTimeout)
        return currentTimeNow.time >= order.lastUpdateDate
    }

    private fun openOrder(messageDto: MessageDto, order: OrderEntity): SendMessage {
        order.status = OrderStatus.Open
        orderRepository.persist(order)
        return MessageUtils.createMessage(
            messageDto,
            "Successfully opened order '${order.name}'"
        )
    }
}
