package it.bot.service.impl

import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.logging.Log
import it.bot.service.interfaces.AllYouCanEatBotService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
@IfBuildProperty(name = "bot.type", stringValue = "polling")
class PollingAllYouCanEatBotService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.token") private val botToken: String,
    updateParserService: UpdateParserService,
) : TelegramLongPollingBot(), AllYouCanEatBotService {

    private val updateHandler = UpdateHandler(updateParserService)

    init {
        Log.info("Init polling bot")
    }

    override fun handleUpdate(update: Update) {
        onUpdateReceived(update)
    }

    override fun onUpdateReceived(update: Update) {
        updateHandler.handleUpdate(this, update)
    }

    override fun getBotUsername(): String {
        return botUsername
    }

    override fun getBotToken(): String {
        return botToken
    }
}
