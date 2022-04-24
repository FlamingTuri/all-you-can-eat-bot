package it.bot.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import it.bot.model.entity.DishEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DishRepository : PanacheRepository<DishEntity> {

    fun findDish(menuNumber: Int, orderId: Long): DishEntity? {
        return find("menuNumber = ?1 and orderId = ?2", menuNumber, orderId).firstResult()
    }
}
