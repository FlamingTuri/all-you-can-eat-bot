package it.bot.service.impl

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.runtime.Startup
import it.bot.AllYouCanEatBot
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@Suppress("unused")
@Startup
@ApplicationScoped
@UnlessBuildProfile("test")
class BotService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @Inject private val updateParserService: UpdateParserService,
    @Inject private val botCommandsService: BotCommandsService
) {

    private val botsApi = TelegramBotsApi(DefaultBotSession::class.java)

    init {
        botCommandsService.setBotSupportedCommands()

        botsApi.registerBot(AllYouCanEatBot(botUsername, botToken, updateParserService))
    }
}
