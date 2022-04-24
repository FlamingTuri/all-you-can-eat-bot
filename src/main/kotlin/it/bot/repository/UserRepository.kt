package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.UserEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository : PanacheRepository<UserEntity> {

    fun findUser(telegramUserId: Long): UserEntity? {
        return find("telegramUserId = ?1", telegramUserId).firstResult()
    }
}
