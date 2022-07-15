package it.bot.service.impl.command

import io.quarkus.logging.Log
import it.bot.model.command.CreateOrderCommand
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.model.messages.OrderMessages
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CreateOrderService(private val orderRepository: OrderRepository) : CommandParserService {

    override val botCommand = CreateOrderCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        return createOrderIfNotExists(messageDto, matchResult)
    }

    private fun createOrderIfNotExists(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val orderName = destructure(matchResult)

        if (checkIfOrderAlreadyExists(messageDto, orderName)) {
            return MessageUtils.createMessage(messageDto, OrderMessages.orderWithTheSameNameError)
        }

        createOrder(messageDto, orderName)

        return MessageUtils.createMessage(messageDto, OrderMessages.orderCreationSuccessful(orderName))
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, _, orderName, _) = matchResult.destructured
        return orderName
    }

    private fun checkIfOrderAlreadyExists(messageDto: MessageDto, orderName: String): Boolean {
        return orderRepository.existsOrderWithNameForChat(MessageUtils.getChatId(messageDto), orderName)
    }

    private fun createOrder(messageDto: MessageDto, orderName: String) {
        Log.info("creating order with name $orderName for chat ${MessageUtils.getChatId(messageDto)}")

        val order = OrderEntity().apply {
            name = orderName
            chatId = MessageUtils.getChatId(messageDto)
            status = OrderStatus.Open
        }

        orderRepository.persist(order)
    }
}
