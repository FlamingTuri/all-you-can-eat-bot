package it.bot.service.impl

import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import it.bot.model.command.BotCommand
import it.bot.model.command.MyOrdersCommand
import it.bot.model.entity.OrderEntity
import it.bot.model.entity.UserDishEntity
import it.bot.repository.OrderRepository
import it.bot.repository.UserDishRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class MyOrdersService(
    @Inject private val orderRepository: OrderRepository,
    @Inject private val userDishRepository: UserDishRepository
) : CommandParserService() {

    override val botCommand: BotCommand = MyOrdersCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val telegramUserId = MessageUtils.getTelegramUserId(update)
        val sort = Sort.by("status")
        val orders = orderRepository.findOrderForUser(telegramUserId, null, sort)

        val messageText = when {
            orders.isEmpty() -> getNoOrderToDisplayMessage(update)
            else -> formatUserOrders(update, orders)
        }

        return MessageUtils.createMessage(update, messageText).apply {
            enableMarkdown(true)
        }
    }

    private fun getNoOrderToDisplayMessage(update: Update): String {
        Log.info("User ${MessageUtils.getTelegramUserId(update)} does not have any order")
        return "You do not have any order to display"
    }

    private fun formatUserOrders(update: Update, orders: List<OrderEntity>): String {
        val telegramUserId = MessageUtils.getTelegramUserId(update)
        val userOrdersIds = orders.map { it.orderId }
        val sort = Sort.by("d.menuNumber")
        val userDishes = userDishRepository.findUserDishes(telegramUserId, userOrdersIds, sort)

        return orders.joinToString("\n\n") { formatOrder(it, userDishes) }
    }

    private fun formatOrder(order: OrderEntity, userDishes: List<UserDishEntity>): String {
        val orderUserDishes = userDishes.filter {
            it.dish!!.orderId == order.orderId
        }
        return "*${order.name}:*\n${formatDishInfo(orderUserDishes)}"
    }

    private fun formatDishInfo(userDishes: List<UserDishEntity>): String {
        return userDishes.joinToString("\n") {
            when (it.dish) {
                null -> ""
                else -> "- number ${it.dish!!.menuNumber}${formatDishName(it.dish!!.name)} x ${it.quantity}"
            }
        }
    }

    private fun formatDishName(dishName: String?): String {
        return if (dishName == null) "" else " ($dishName)"
    }
}
