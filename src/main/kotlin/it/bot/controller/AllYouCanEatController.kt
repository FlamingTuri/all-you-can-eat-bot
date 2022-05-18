package it.bot.controller

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import it.bot.service.impl.BotService
import it.bot.util.Constants
import org.telegram.telegrambots.meta.api.objects.Update
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response


@UnlessBuildProfile("test")
@Startup
@Path(Constants.basePath)
class AllYouCanEatController(@Inject private val botService: BotService) {

    @GET
    @Path("/status")
    fun getStatus(): Response {
        Log.info("requested app status")
        return Response.ok("ready").build()
    }

    @POST
    @Path("/callback")
    fun receiveUpdate(update: Update): Response {
        botService.handleUpdate(update)
        return Response.noContent().build()
    }
}
