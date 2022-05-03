package it.bot.controller

import io.quarkus.runtime.Startup
import javax.ws.rs.GET
import javax.ws.rs.Path

@Startup
@Path("/bot")
class AllYouCanEatController {

    @GET
    @Path("/status")
    fun getStatus(): String {
        return "ready"
    }
}
