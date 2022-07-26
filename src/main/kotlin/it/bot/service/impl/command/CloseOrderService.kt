package it.bot.service.impl.command

import io.quarkus.logging.Log
import it.bot.model.command.CloseOrderCommand
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CloseOrderService(
    @ConfigProperty(name = "bot.reopen.order.timeout") private val botReopenOrderTimeout: Int,
    private val orderRepository: OrderRepository,
    private val showOrderService: ShowOrderService
) : CommandParserService {

    override val botCommand = CloseOrderCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        val orderName = destructure(matchResult)
        val order = orderRepository.findOrderWithNameForChat(MessageUtils.getChatId(messageDto), orderName)

        return when {
            order == null -> OrderUtils.getOrderNotFoundMessage(messageDto, orderName)
            order.status == OrderStatus.Closed -> getOrderAlreadyClosedMessage(messageDto, order)
            else -> closeOrder(messageDto, order)
        }
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, _, orderName, _) = matchResult.destructured
        return orderName
    }

    private fun getOrderAlreadyClosedMessage(messageDto: MessageDto, order: OrderEntity): SendMessage {
        Log.error("order ${order.orderId} has been already closed for chatId ${MessageUtils.getChatId(messageDto)}")
        return MessageUtils.createMessage(
            messageDto,
            "Error: order '${order.name}' has been already closed"
        )
    }

    private fun closeOrder(messageDto: MessageDto, order: OrderEntity): SendMessage {
        order.status = OrderStatus.Closed
        orderRepository.persist(order)

        val orderDishes = showOrderService.groupOrderDishesByMenuNumber(order)

        val messageText = "Successfully closed order '${order.name}'. " +
                "If you closed it by accident, you have $botReopenOrderTimeout minutes to open it back." +
                "\n\n${OrderUtils.createOrderRecap(orderDishes)}"

        return MessageUtils.createMessage(messageDto, messageText)
    }
}
