package it.bot.controller

import it.bot.service.impl.BotService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("bot")
class BotController {

    @Inject
    private var botService: BotService? = null

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        return "Hello World"
    }
}