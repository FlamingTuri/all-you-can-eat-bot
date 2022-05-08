package it.bot.job

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import it.bot.repository.DishRepository
import it.bot.repository.OrderRepository
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


@UnlessBuildProfile("test")
@Suppress("unused")
@ApplicationScoped
class CleanupJob(
    @ConfigProperty(name = "bot.order.deletion.timeout") private val hours: Int,
    @Inject val orderRepository: OrderRepository,
    @Inject val userRepository: UserRepository,
    @Inject val dishRepository: DishRepository,
    @Inject val userDishRepository: UserDishRepository,
) {

    @Transactional
    @Scheduled(cron = "{bot.cleanup.job.cron.expr}")
    fun cleanup() {
        Log.info("CleanupJob: started, hours configured for order deletion: $hours")
        val orders = orderRepository.findOrdersNotUpdatedFor(hours)
        Log.info("CleanupJob: deleting ${orders.size} orders")
        orders.forEach { order ->
            order.orderId?.let {
                Log.info("CleanupJob: deleting order $it, last update ${order.lastUpdateDate}")
                userDishRepository.deleteOrderUserDishes(it)
                dishRepository.deleteOrderDishes(it)
                userRepository.deleteOrderUsers(it)
                orderRepository.deleteById(it)
            }
        }
        Log.info("CleanupJob: finished")
    }
}
