package it.bot.controller

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import it.bot.service.impl.BotService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.annotations.jaxrs.PathParam
import org.telegram.telegrambots.meta.api.objects.Update
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

@UnlessBuildProfile("test")
@Startup
@Path("/callback")
class WebHookController(
    @ConfigProperty(name = "bot.webhook") private val botWebhook: String,
    @Inject private val botService: BotService
) {

    @POST
    @Path("/{botPath}")
    fun receiveUpdate(@PathParam botPath: String, update: Update): Response {
        val response = if (botPath == botWebhook) {
            Log.info("received webhook update")
            botService.callBack(update)
            Response.noContent()
        } else {
            Response.status(Status.NOT_FOUND.statusCode)
        }
        return response.build()
    }
}
