package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.CommandCacheEntity
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CommandCacheRepository : PanacheRepository<CommandCacheEntity> {

    fun findChatUserCommand(chatId: Long, telegramUserId: Long, minutes: Int): CommandCacheEntity? {
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.MINUTE, -minutes)
        val query = "chatId = ?1 and telegramUserId = ?2 and lastUpdateDate >= ?3"
        return find(query, chatId, telegramUserId, currentTimeNow.time).firstResult()
    }

    fun deleteChatUserCommand(chatId: Long, telegramUserId: Long) {
        delete("chatId = ?1 and telegramUserId = ?2", chatId, telegramUserId)
    }

    fun deleteElapsedChatUserCommand(minutes: Int) {
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.MINUTE, -minutes)
        delete("lastUpdateDate <= ?1", currentTimeNow.time)
    }
}
