package it.bot.service.impl

import io.quarkus.arc.profile.IfBuildProfile
import it.bot.service.interfaces.AllYouCanEatBotService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
@IfBuildProfile("polling")
class PollingAllYouCanEatBotService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @Inject private val updateParserService: UpdateParserService,
) : TelegramLongPollingBot(), AllYouCanEatBotService {

    private val updateHandler = UpdateHandler(updateParserService)

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
