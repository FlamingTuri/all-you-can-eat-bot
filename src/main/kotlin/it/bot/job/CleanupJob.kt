package it.bot.job

import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class CleanupJob {

    @Scheduled(cron = "{cron.expr}")
    fun cleanup() {
        Log.info("Cron expression")
    }
}
