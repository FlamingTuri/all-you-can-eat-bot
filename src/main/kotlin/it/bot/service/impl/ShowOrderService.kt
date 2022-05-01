package it.bot.service.impl

import it.bot.model.command.ShowOrderCommand
import it.bot.model.dto.DishDto
import it.bot.model.entity.OrderEntity
import it.bot.repository.DishJpaRepository
import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class ShowOrderService(
    @Inject private val orderRepository: OrderRepository,
    @Inject private val dishJpaRepository: DishJpaRepository,
) : CommandParserService() {

    override val botCommand = ShowOrderCommand()

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val (_, orderName, _) = matchResult.destructured

        return when (val order = orderRepository.findOrderWithNameForChat(MessageUtils.getChatId(update), orderName)) {
            null -> OrderUtils.getOrderNotFoundMessage(update, orderName)
            else -> {
                val orderDishes = groupOrderDishesByMenuNumber(order)

                val messageText = "Order '${order.name}' (${order.status})" +
                        "\n\n${OrderUtils.createOrderRecap(orderDishes)}"

                MessageUtils.createMessage(update, messageText)
            }
        }

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
