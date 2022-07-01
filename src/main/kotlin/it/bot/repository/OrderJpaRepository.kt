package it.bot.repository

import it.bot.model.entity.OrderEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Date
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {

    @Query(
        """
            select o.orderId
            from OrderEntity o
            where o.lastUpdateDate <= :date
        """,
        countQuery = """
            select count(o)
            from OrderEntity o
            where o.lastUpdateDate <= :date
        """
    )
    fun findOrderIdsNotUpdatedFor(date: Date, pageable: Pageable): Page<Long>
}
