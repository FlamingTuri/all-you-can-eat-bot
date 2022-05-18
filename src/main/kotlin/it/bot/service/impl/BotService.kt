package it.bot.service.impl

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.Startup
import it.bot.service.interfaces.AllYouCanEatBotService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook
import org.telegram.telegrambots.util.WebhookUtils
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject


@Suppress("unused")
@Startup
@ApplicationScoped
@UnlessBuildProfile("test")
class BotService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @ConfigProperty(name = "bot.webhook") private val botWebhook: String,
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
            url = botWebhook
        }
        botsApi.registerBot(bot, setWebhook)
    }

    fun handleUpdate(update: Update) {
        allYouCanEatBotService.handleUpdate(update)
    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        Log.info("The application is stopping...")
        when (allYouCanEatBotService) {
            is WebhookAllYouCanEatBotService -> WebhookUtils.clearWebhook(allYouCanEatBotService)
        }
    }
}
