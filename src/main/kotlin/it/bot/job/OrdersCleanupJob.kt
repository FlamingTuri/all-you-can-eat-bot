package it.bot.job

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import it.bot.repository.DishRepository
import it.bot.repository.OrderJpaRepository
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.springframework.data.domain.PageRequest
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional


@UnlessBuildProfile("test")
@Suppress("unused")
@ApplicationScoped
class OrdersCleanupJob(
    @ConfigProperty(name = "bot.orders.deletion.timeout") private val hours: Int,
    private val orderRepository: OrderJpaRepository,
    private val userRepository: UserRepository,
    private val dishRepository: DishRepository,
    private val userDishRepository: UserDishRepository,
) {

    @Transactional
    @Scheduled(cron = "{bot.orders.cleanup.job.cron.expr}")
    fun cleanup() {
        Log.info("OrdersCleanupJob: started, hours configured for order deletion: $hours")
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.HOUR, -hours)
        val page = PageRequest.of(0, 50)

        do {
            val orderIds = orderRepository.findOrderIdsNotUpdatedFor(currentTimeNow.time, page).content
            Log.info("OrdersCleanupJob: deleting ${orderIds.size} orders")
            orderIds.forEach { orderId ->
                Log.info("OrdersCleanupJob: deleting order $orderId")
                userDishRepository.deleteOrderUserDishes(orderId)
                dishRepository.deleteOrderDishes(orderId)
                userRepository.deleteOrderUsers(orderId)
                orderRepository.deleteById(orderId)
            }
        } while (orderIds.isNotEmpty())

        Log.info("OrdersCleanupJob: finished")
    }
}
