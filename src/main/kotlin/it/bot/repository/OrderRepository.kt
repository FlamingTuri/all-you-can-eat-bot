package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.Order
import it.bot.model.enum.OrderStatus
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderRepository : PanacheRepository<Order> {

    fun existsOrderWithNameForChat(chatId: Long, orderName: String): Boolean {
        return find("chatId = ?1 and name = ?2", chatId, orderName).firstResult() != null
    }

    fun findOpenOrderWithNameForChat(chatId: Long, orderName: String): Order? {
        return find("chatId = ?1 and name = ?2 and status = ?3", chatId, orderName, OrderStatus.Open).firstResult()
    }
}
