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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class CreateOrderService(@Inject private val orderRepository: OrderRepository) : CommandParserService() {

    override val command: String = "/createOrder"

    override val commandPattern: String = "(\\s*)(\\w+)(\\s*)"

    override val commandFormat: String = "{orderName}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        return createOrderIfNotExists(update, matchResult)
    }

    private fun createOrderIfNotExists(update: Update, matchResult: MatchResult): SendMessage {
        val (_, orderName, _) = matchResult.destructured

        if (checkIfOrderAlreadyExists(update, orderName)) {
            return MessageUtils.createMessage(
                update,
                "Error: an order with the same name already exists for current chat"
            )
        }

        createOrder(update, orderName)
        return MessageUtils.createMessage(update, "Successfully created order '$orderName'")
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
