package it.bot.service.impl

import io.quarkus.arc.profile.IfBuildProfile
import io.quarkus.logging.Log
import it.bot.service.interfaces.AllYouCanEatBotService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
//@IfBuildProfile("webhook")
class WebhookAllYouCanEatBotService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @ConfigProperty(name = "bot.webhook.callback.path") private val botWebhookCallbackPath: String,
    @Inject private val updateParserService: UpdateParserService
) : TelegramWebhookBot(), AllYouCanEatBotService {

    private val updateHandler = UpdateHandler(updateParserService)

    override fun onWebhookUpdateReceived(update: Update?): BotApiMethod<*>? {
        update?.let { updateHandler.handleUpdate(this, it) }
        return null
    }

    override fun getBotUsername(): String {
        return botUsername
    }

    override fun getBotToken(): String {
        return botToken
    }

    override fun getBotPath(): String {
        return botWebhookCallbackPath
    }
}
