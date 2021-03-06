package it.bot.job

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import it.bot.client.rest.AllYouCanEatBotRestClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional


@UnlessBuildProfile("test")
@Suppress("unused")
@ApplicationScoped
class KeepAliveJob(
    @RestClient private val allYouCanEatBotRestClient: AllYouCanEatBotRestClient
) {

    @Transactional
    @Scheduled(cron = "{bot.keep.alive.job.cron.expr}")
    fun cleanup() {
        Log.info("KeepAliveJob: started")
        allYouCanEatBotRestClient.getStatus()
        Log.info("KeepAliveJob: finished")
    }
}
