package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.DishEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DishRepository : PanacheRepository<DishEntity> {

    fun findDish(menuNumber: Int, orderId: Long): DishEntity? {
        return find("menuNumber = ?1 and orderId = ?2", menuNumber, orderId).firstResult()
    }

    fun updateDishName(menuNumber: Int, dishName: String, orderId: Long) {
        val query = """
            update DishEntity d 
            set d.name = ?1
            where d.menuNumber = ?2 and d.orderId = ?3
        """
        update(query, dishName, menuNumber, orderId)
    }

    fun deleteOrderDishes(orderId: Long) {
        val query = """
            delete DishEntity
            where dishId in (
                select d.dishId
                from DishEntity d
                where d.orderId = ?1
            )
        """
        delete(query, orderId)
    }
}
