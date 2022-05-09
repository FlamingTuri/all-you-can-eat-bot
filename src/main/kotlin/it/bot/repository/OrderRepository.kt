package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Sort
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderRepository : PanacheRepository<OrderEntity> {

    fun existsOrderWithNameForChat(chatId: Long, orderName: String): Boolean {
        return find("chatId = ?1 and name = ?2", chatId, orderName).firstResult() != null
    }

    fun findOrders(chatId: Long): List<OrderEntity> {
        return find("chatId = ?1", chatId).list()
    }

    fun findOrderWithNameForChat(chatId: Long, orderName: String): OrderEntity? {
        return find("chatId = ?1 and name = ?2", chatId, orderName).firstResult()
    }

    fun findOpenOrderWithNameForChat(chatId: Long, orderName: String): OrderEntity? {
        return find("chatId = ?1 and name = ?2 and status = ?3", chatId, orderName, OrderStatus.Open).firstResult()
    }

    fun findActiveOrderWithNameForChat(chatId: Long, orderName: String, botReopenOrderTimeout: Int): OrderEntity? {
        val query = """
            chatId = ?1
            and name = ?2
            and (status = 'Open' or (status = 'Closed' and ?3 < lastUpdateDate))
        """
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.MINUTE, -botReopenOrderTimeout)
        return find(query, chatId, orderName, currentTimeNow).firstResult()
    }

    fun findOrderForUser(telegramUserId: Long, orderName: String?, sort: Sort): List<OrderEntity> {
        val query = """
            select o
            from OrderEntity o
            join UserEntity u on u.orderId = o.orderId
            where u.telegramUserId = ?1
            and (?2 = '' or o.name = ?2)
        """
        return find(query, sort, telegramUserId, orderName ?: "").list()
    }

    fun findOrdersNotUpdatedFor(hours: Int): List<OrderEntity> {
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.HOUR, -hours)
        return find("lastUpdateDate <= ?1", currentTimeNow.time).list()
    }
}
