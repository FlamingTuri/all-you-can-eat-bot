package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.command.CloseOrderCommand
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class CloseOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    @Inject private val orderRepository: OrderRepository,
    @Inject private val showOrderService: ShowOrderService
) : CommandParserService {

    override val botCommand = CloseOrderCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val (_, orderName, _) = matchResult.destructured
        val order = orderRepository.findOrderWithNameForChat(MessageUtils.getChatId(update), orderName)

        return when {
            order == null -> OrderUtils.getOrderNotFoundMessage(update, orderName)
            order.status == OrderStatus.Close -> getOrderAlreadyClosedMessage(update, order)
            else -> closeOrder(update, order)
        }
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

        val orderDishes = showOrderService.groupOrderDishesByMenuNumber(order)

        val messageText = "Successfully closed order '${order.name}'. " +
                "If you closed it by accident, you have $botReopenOrderTimeout minutes to open it back." +
                "\n\n${OrderUtils.createOrderRecap(orderDishes)}"

        return MessageUtils.createMessage(update, messageText)
    }
}
