package it.bot.service.impl.command

import it.bot.model.command.ShowOrderCommand
import it.bot.model.dto.DishDto
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.repository.DishJpaRepository
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShowOrderService(
    private val orderRepository: OrderRepository,
    private val dishJpaRepository: DishJpaRepository,
) : CommandParserService {

    override val botCommand = ShowOrderCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val orderName = destructure(matchResult)

        return when (val order = orderRepository.findOrderWithNameForChat(MessageUtils.getChatId(messageDto), orderName)) {
            null -> OrderUtils.getOrderNotFoundMessage(messageDto, orderName)
            else -> {
                val orderDishes = groupOrderDishesByMenuNumber(order)

                val messageText = "Order '${order.name}' (${order.status})" +
                        "\n\n${OrderUtils.createOrderRecap(orderDishes)}"

                MessageUtils.createMessage(messageDto, messageText)
            }
        }
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, _, orderName, _) = matchResult.destructured
        return orderName
    }

    fun groupOrderDishesByMenuNumber(order: OrderEntity): List<DishDto> {
        return dishJpaRepository.groupOrderDishesByMenuNumber(order.orderId!!).map {
            val menuNumber = it[0] as Int
            val quantity = it[1] as Long
            val name = it[2] as String
            DishDto(menuNumber, quantity, name)
        }
    }
}
