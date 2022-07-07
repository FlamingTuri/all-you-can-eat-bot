package it.bot.job

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import it.bot.repository.CommandCacheRepository
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional


@UnlessBuildProfile("test")
@Suppress("unused")
@ApplicationScoped
class CommandCacheCleanupJob(
    @ConfigProperty(name = "bot.command.cache.validity.time") private val minutes: Int,
    private val commandCacheRepository: CommandCacheRepository
) {

    @Transactional
    @Scheduled(cron = "{bot.command.cache.cleanup.job.cron.expr}")
    fun cleanup() {
        Log.info("CleanupCommandCacheJob: started, minutes configured for command cache deletion: $minutes")
        commandCacheRepository.deleteElapsedChatUserCommand(minutes)
        Log.info("CleanupCommandCacheJob: finished")
    }
}
