package it.bot.job

import io.quarkus.arc.properties.UnlessBuildProperty
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import it.bot.client.rest.AllYouCanEatBotRestClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@UnlessBuildProperty(name = "quarkus.profile", stringValue = "test")
@Suppress("unused")
@ApplicationScoped
class KeepAliveJob(
    @Inject @RestClient private val allYouCanEatBotRestClient: AllYouCanEatBotRestClient
) {

    @Transactional
    @Scheduled(cron = "{bot.keep.alive.job.cron.expr}")
    fun cleanup() {
        Log.info("KeepAliveJob: started")
        allYouCanEatBotRestClient.getStatus()
        Log.info("KeepAliveJob: finished")
    }
}
