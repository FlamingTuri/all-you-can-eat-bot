package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.CommandCacheEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CommandCacheRepository : PanacheRepository<CommandCacheEntity> {

    fun findChatUserCommand(chatId: Long, telegramUserId: Long): CommandCacheEntity? {
        return find("chatId = ?1 and telegramUserId = ?2", chatId, telegramUserId).firstResult()
    }

    fun deleteChatUserCommand(chatId: Long, telegramUserId: Long) {
        delete("chatId = ?1 and telegramUserId = ?2", chatId, telegramUserId)
    }
}
