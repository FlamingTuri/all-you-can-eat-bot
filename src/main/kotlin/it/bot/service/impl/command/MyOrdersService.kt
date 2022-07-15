package it.bot.service.impl.command

import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import it.bot.model.command.BotCommand
import it.bot.model.command.MyOrdersCommand
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.model.entity.UserDishEntity
import it.bot.repository.OrderRepository
import it.bot.repository.UserDishRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class MyOrdersService(
    private val orderRepository: OrderRepository,
    private val userDishRepository: UserDishRepository
) : CommandParserService {

    override val botCommand: BotCommand = MyOrdersCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val orderName = destructure(matchResult)

        val telegramUserId = MessageUtils.getTelegramUserId(messageDto)
        val sort = Sort.by("status")
        val orders = orderRepository.findOrderForUser(telegramUserId, orderName, sort)

        val messageText = when {
            orders.isEmpty() -> getNoOrderToDisplayMessage(messageDto, orderName)
            else -> formatUserOrders(messageDto, orders)
        }

        return MessageUtils.createMessage(messageDto, messageText).apply {
            enableMarkdown(true)
        }
    }

    private fun destructure(matchResult: MatchResult): String? {
        val (_, _, orderName, _) = matchResult.destructured
        return if (orderName == "") null else orderName
    }

    private fun getNoOrderToDisplayMessage(messageDto: MessageDto, orderName: String?): String {
        Log.info("User ${MessageUtils.getTelegramUserId(messageDto)} does not have any order, orderName '$orderName'")
        return when (orderName) {
            null -> "You do not have any order to display"
            else -> "You have not joined order '$orderName'"
        }
    }

    private fun formatUserOrders(messageDto: MessageDto, orders: List<OrderEntity>): String {
        val telegramUserId = MessageUtils.getTelegramUserId(messageDto)
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
