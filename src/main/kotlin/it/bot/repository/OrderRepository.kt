package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderRepository : PanacheRepository<OrderEntity> {

    fun existsOrderWithNameForChat(chatId: Long, orderName: String): Boolean {
        return find("chatId = ?1 and name = ?2", chatId, orderName).firstResult() != null
    }

    fun findOpenOrderWithNameForChat(chatId: Long, orderName: String): OrderEntity? {
        return find("chatId = ?1 and name = ?2 and status = ?3", chatId, orderName, OrderStatus.Open).firstResult()
    }
}
