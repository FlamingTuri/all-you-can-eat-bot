package it.bot.service.impl

import it.bot.model.command.BotCommand
import it.bot.model.command.ChatOrdersCommand
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class ChatOrdersService(@Inject private val orderRepository: OrderRepository) : CommandParserService {

    override val botCommand: BotCommand = ChatOrdersCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val orders = orderRepository.findOrders(MessageUtils.getChatId(update))
        val messageText = when {
            orders.isEmpty() -> "The current chat does not have any order"
            else -> formatChatOrders(orders)
        }
        return MessageUtils.createMessage(update, messageText).apply {
            enableMarkdown(true)
        }
    }

    private fun formatChatOrders(orders: List<OrderEntity>): String {
        return orders.groupBy { it.status }.map { entry ->
            val status = entry.key
            val orderLines = entry.value
            "${formatStatus(status)}\n${formatOrderLines(orderLines)}"
        }.joinToString("\n\n")
    }

    private fun formatStatus(orderStatus: OrderStatus): String {
        val e = when (orderStatus) {
            OrderStatus.Open -> " (click the highlighted text to copy it)"
            else -> ""
        }
        return "*$orderStatus orders:*$e"
    }

    private fun formatOrderLines(orders: List<OrderEntity>): String {
        return orders.joinToString("\n") {
            when (it.status) {
                OrderStatus.Open -> "- ${it.name}: `/joinOrder ${it.name}`"
                else -> "- ${it.name}"
            }
        }
    }
}
