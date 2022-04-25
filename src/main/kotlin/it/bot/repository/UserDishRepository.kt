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

    fun findUserDishes(menuNumber: Int, chatId: Long): List<UserDishEntity> {
        val query = """
            select ud
            from UserDishEntity ud
            right join DishEntity d on d.dishId = ud.dishId
            join OrderEntity o on o.orderId = d.orderId
            where o.chatId = ?1
            and d.menuNumber = ?2
        """
        return find(query, chatId, menuNumber).list()
    }
}