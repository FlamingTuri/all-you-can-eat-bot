package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.DishEntity
import it.bot.model.entity.UserDishEntity
import it.bot.model.entity.UserEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserDishRepository : PanacheRepository<UserDishEntity> {

    fun findUserDish(user: UserEntity, dish: DishEntity): UserDishEntity? {
        return find("userId = ?1 and dishId = ?2", user.userId!!, dish.dishId!!).firstResult()
    }
}