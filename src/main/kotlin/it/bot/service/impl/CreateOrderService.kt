package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.command.CreateOrderCommand
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.model.messages.OrderMessages
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class CreateOrderService(@Inject private val orderRepository: OrderRepository) : CommandParserService() {

    override val botCommand = CreateOrderCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        return createOrderIfNotExists(update, matchResult)
    }

    private fun createOrderIfNotExists(update: Update, matchResult: MatchResult): SendMessage {
        val (_, orderName, _) = matchResult.destructured

        if (checkIfOrderAlreadyExists(update, orderName)) {
            return MessageUtils.createMessage(update, OrderMessages.orderWithTheSameNameError)
        }

        createOrder(update, orderName)
        return MessageUtils.createMessage(update, OrderMessages.orderCreationSuccessful(orderName))
    }

    private fun checkIfOrderAlreadyExists(update: Update, orderName: String): Boolean {
        return orderRepository.existsOrderWithNameForChat(update.message.chatId, orderName)
    }

    private fun createOrder(update: Update, orderName: String) {
        Log.info("creating order with name $orderName for chat ${MessageUtils.getChatId(update)}")

        val order = OrderEntity().apply {
            name = orderName
            chatId = MessageUtils.getChatId(update)
            status = OrderStatus.Open
        }

        orderRepository.persist(order)
    }
}
