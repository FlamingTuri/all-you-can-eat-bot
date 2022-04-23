package it.bot.service.impl

import io.quarkus.runtime.Startup
import it.bot.AllYouCanEatBot
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


@Startup
@ApplicationScoped
class BotService(
    @ConfigProperty(name = "bot.token")
    private val botToken: String,
    @Inject val createOrderService: CreateOrderService
) {

    private val botsApi = TelegramBotsApi(DefaultBotSession::class.java)

    init {
        botsApi.registerBot(AllYouCanEatBot(botToken, createOrderService))
    }
}