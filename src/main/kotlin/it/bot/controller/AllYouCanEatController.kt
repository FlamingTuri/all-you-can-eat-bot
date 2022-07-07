package it.bot.controller

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import it.bot.service.impl.BotService
import it.bot.util.Constants
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.annotations.jaxrs.PathParam
import org.telegram.telegrambots.meta.api.objects.Update
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response


@UnlessBuildProfile("test")
@Startup
@Path(Constants.basePath)
class AllYouCanEatController(
    @ConfigProperty(name = "bot.token") private val botToken: String,
    private val botService: BotService
) {

    @GET
    @Path("/status")
    fun getStatus(): Response {
        Log.info("requested app status")
        return Response.ok("ready").build()
    }

    @POST
    @Path("/callback/{botPath}")
    fun receiveUpdate(@PathParam botPath: String, update: Update): Response {
        val response = if (botPath == botToken) {
            botService.handleUpdate(update)
            Response.noContent()
        } else {
            Response.status(Response.Status.NOT_FOUND.statusCode)
        }
        return response.build()
    }
}
