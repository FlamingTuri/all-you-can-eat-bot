package it.bot.client.rest

import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.telegram.telegrambots.meta.api.objects.ApiResponse
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand

@ApplicationScoped
@RegisterRestClient(configKey = "telegram-rest-client")
interface TelegramRestClient {

    @GET
    @Path("/bot{botToken}/setMyCommands")
    fun setBotCommands(
        @PathParam("botToken") botToken: String,
        @QueryParam("commands") commands: String,
    ): ApiResponse<Boolean>

    @GET
    @Path("/bot{botToken}/getChatMember")
    fun getChatMember(
        @PathParam("botToken") botToken: String,
        @QueryParam("chat_id") chatId: Long,
        @QueryParam("user_id") userId: Long
    ): ApiResponse<Map<String, Any>>
}