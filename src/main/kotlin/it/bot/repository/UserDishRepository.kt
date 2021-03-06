package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Sort
import it.bot.model.entity.DishEntity
import it.bot.model.entity.UserDishEntity
import it.bot.model.entity.UserEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserDishRepository : PanacheRepository<UserDishEntity> {

    fun findUserDish(user: UserEntity, dish: DishEntity): UserDishEntity? {
        return find("userId = ?1 and dishId = ?2", user.userId!!, dish.dishId!!).firstResult()
    }

    fun findUserDish(menuNumber: Int, telegramUserId: Long): UserDishEntity? {
        val query = """
            select ud
            from UserDishEntity ud
            join DishEntity d on d.dishId = ud.dishId
            join OrderEntity o on o.orderId = d.orderId
            join UserEntity u on u.orderId = o.orderId
            where u.telegramUserId = ?1
            and d.menuNumber = ?2
        """
        return find(query, telegramUserId, menuNumber).firstResult()
    }

    fun findUserDishes(menuNumber: Int, orderName: String?, chatId: Long): List<UserDishEntity> {
        val query = """
            select ud
            from UserDishEntity ud
            right join DishEntity d on d.dishId = ud.dishId
            join OrderEntity o on o.orderId = d.orderId
            where o.chatId = ?1
            and d.menuNumber = ?2
            and (?3 = '' or o.name = ?3)
        """
        return find(query, chatId, menuNumber, orderName ?: "").list()
    }

    fun checkIfOrderHasDishes(userDish: UserDishEntity): Boolean {
        return checkIfOrderHasDishes(userDish.user?.orderId, userDish.dish?.menuNumber)
    }

    private fun checkIfOrderHasDishes(orderId: Long?, menuNumber: Int?): Boolean {
        val query = """
            select ud
            from UserDishEntity ud
            join DishEntity d on d.dishId = ud.dishId
            join OrderEntity o on o.orderId = d.orderId
            where o.orderId = ?1
            and d.menuNumber = ?2
        """
        return find(query, orderId!!, menuNumber!!).count() > 0
    }

    fun checkIfOrderHasUserDishes(orderId: Long, userId: Long): Boolean {
        val query = """
            select ud
            from UserDishEntity ud
            join UserEntity u on u.userId = ud.userId
            where u.orderId = ?1
            and u.userId = ?2
        """
        return find(query, orderId, userId).count() > 0
    }

    fun deleteOrderUserDishes(orderId: Long) {
        val query = """
            delete UserDishEntity
            where userDishId in (
                select ud.userDishId
                from UserDishEntity ud
                join DishEntity d on d.dishId = ud.dishId
                where d.orderId = ?1
            )
        """
        delete(query, orderId)
    }

    fun findUserDishes(telegramUserId: Long, orderIds: List<Long?>, sort: Sort): List<UserDishEntity> {
        val query = """
            select ud
            from UserDishEntity ud
            join UserEntity u on u.userId = ud.userId
            join DishEntity d on d.dishId = ud.dishId
            where u.telegramUserId = ?1
            and d.orderId in (?2)
        """
        return find(query, sort, telegramUserId, orderIds).list()
    }
}
