package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.UserEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository : PanacheRepository<UserEntity> {

    fun findUser(telegramUserId: Long): UserEntity? {
        return find("telegramUserId = ?1", telegramUserId).firstResult()
    }

    fun findUser(telegramUserId: Long, chatId: Long, orderName: String): UserEntity? {
        val query = """
            select u
            from UserEntity u
            join OrderEntity o on o.orderId = u.orderId
            where u.telegramUserId = ?1
            and o.chatId = ?2
            and o.name = ?3
        """
        return find(query, telegramUserId, chatId, orderName).firstResult()
    }

    fun findUsers(telegramUserId: Long): List<UserEntity> {
        val query = "telegramUserId = ?1"
        return find(query, telegramUserId).list()
    }

    fun findUsers(telegramUserId: Long, chatId: Long): List<UserEntity> {
        val query = """
            select u
            from UserEntity u
            join OrderEntity o on o.orderId = u.orderId
            where u.telegramUserId = ?1
            and o.chatId = ?2
        """
        return find(query, telegramUserId, chatId).list()
    }

    fun deleteOrderUsers(orderId: Long) {
        val query = """orderId = ?1"""
        delete(query, orderId)
    }
}
