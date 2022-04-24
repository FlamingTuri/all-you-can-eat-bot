package it.bot.repository

import it.bot.model.entity.DishEntity
import javax.enterprise.context.ApplicationScoped
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@ApplicationScoped
interface DishJpaRepository : JpaRepository<DishEntity, Long> {

    @Query(
        """
            select new List(d.menuNumber , sum(uds.quantity) , d.name )
            from DishEntity d
            join fetch UserDishEntity uds on d.dishId = uds.dishId
            where d.orderId = ?1
            group by d.menuNumber, d.name
        """
    )
    fun groupOrderDishesByMenuNumber(orderId: Long): List<List<Any>>
}