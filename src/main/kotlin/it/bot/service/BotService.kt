package it.bot.service

import io.quarkus.runtime.Startup
import it.bot.AllYouCanEatBot
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.enterprise.context.ApplicationScoped


@Startup
@ApplicationScoped
class BotService(@ConfigProperty(name = "bot.token") val botToken: String) {

    private val botsApi = TelegramBotsApi(DefaultBotSession::class.java)

    init {
        botsApi.registerBot(AllYouCanEatBot(botToken))
    }
}