package it.bot.job

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import it.bot.repository.CommandCacheRepository
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
class CleanupCommandCacheJob(
    @ConfigProperty(name = "bot.command.cache.deletion.timeout") private val minutes: Int,
    @Inject val commandCacheRepository: CommandCacheRepository
) {

    @Transactional
    @Scheduled(cron = "{bot.cleanup.command.cache.job.cron.expr}")
    fun cleanup() {
        Log.info("CleanupCommandCacheJob: started, minutes configured for command cache deletion: $minutes")
        commandCacheRepository.deleteElapsedChatUserCommand(minutes)
        Log.info("CleanupCommandCacheJob: finished")
    }
}
