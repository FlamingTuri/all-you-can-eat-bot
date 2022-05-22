package it.bot.service.impl

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import it.bot.client.rest.TelegramRestClient
import it.bot.service.interfaces.AllYouCanEatBotService
import it.bot.util.Constants
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@Suppress("unused")
@Startup
@ApplicationScoped
@UnlessBuildProfile("test")
class BotService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @ConfigProperty(name = "bot.host") private val botHost: String,
    @Inject @RestClient private val telegramRestClient: TelegramRestClient,
    @Inject private val updateParserService: UpdateParserService,
    @Inject private val botCommandsService: BotCommandsService,
    @Inject private val allYouCanEatBotService: AllYouCanEatBotService
) {

    private lateinit var botsApi: TelegramBotsApi

    init {
        botCommandsService.setBotSupportedCommands()

        when (allYouCanEatBotService) {
            is PollingAllYouCanEatBotService -> initLongPollingBot(allYouCanEatBotService)
            is WebhookAllYouCanEatBotService -> initWebhookBot(allYouCanEatBotService)
            else -> Log.error("unsupported allYouCanEatBotService")
        }
    }

    private fun initLongPollingBot(bot: LongPollingBot) {
        // telegram does not allow to manually retrieve updates if a webhook is active
        telegramRestClient.deleteWebhook(botToken)

        botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        botsApi.registerBot(bot)
    }

    private fun initWebhookBot(bot: TelegramWebhookBot) {
        val webhook = DefaultWebhook()
        // this is the URL where the application will receive requests
        webhook.setInternalUrl("http://0.0.0.0:8080/")

        botsApi = TelegramBotsApi(DefaultBotSession::class.java, webhook)

        val setWebhook = SetWebhook().apply {
            // this is the public URL accessed by Telegram servers
            // it must be reverse proxied to the internalUrl if they are different
            // the callback url will be $botHost/${Constants.basePath}/callback
            url = "$botHost${Constants.basePath}"
        }
        botsApi.registerBot(bot, setWebhook)
    }

    fun handleUpdate(update: Update) {
        allYouCanEatBotService.handleUpdate(update)
    }
}
