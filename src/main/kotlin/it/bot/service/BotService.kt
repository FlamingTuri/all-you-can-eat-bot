package it.bot.service

import io.quarkus.runtime.Startup
import it.bot.AllYouCanEatBot
import it.bot.repository.OrderRepository
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@Startup
@ApplicationScoped
class BotService(
    @ConfigProperty(name = "bot.token")
    private val botToken: String,
    @Inject val orderRepository: OrderRepository
) {

    private val botsApi = TelegramBotsApi(DefaultBotSession::class.java)

    init {
        botsApi.registerBot(AllYouCanEatBot(botToken, orderRepository))
    }
}