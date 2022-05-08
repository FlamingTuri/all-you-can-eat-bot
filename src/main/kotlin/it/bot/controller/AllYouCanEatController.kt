package it.bot.controller

import io.quarkus.runtime.Startup
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Startup
@Path("/bot")
class AllYouCanEatController {

    @GET
    @Path("/status")
    fun getStatus(): Response {
        return Response.ok("ready").build()
    }
}
