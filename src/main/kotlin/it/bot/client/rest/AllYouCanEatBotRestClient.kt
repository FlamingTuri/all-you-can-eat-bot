package it.bot.client.rest

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.GET
import javax.ws.rs.Path

@ApplicationScoped
@RegisterRestClient(configKey = "all-you-can-eat-bot-rest-client")
interface AllYouCanEatBotRestClient {

    @GET
    @Path("/bot/status")
    fun getStatus(): String
}
