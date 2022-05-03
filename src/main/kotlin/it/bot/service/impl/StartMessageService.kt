package it.bot.service.impl

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import it.bot.model.command.StartMessageCommand
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StartMessageService(
    @ConfigProperty(name = "bot.repo.url", defaultValue = "") val repoUrl: String,
    @ConfigProperty(name = "bot.donate.url", defaultValue = "") val donateUrl: String,
    @Location("welcome.html") val welcomeTemplate: Template
) : CommandParserService() {

    override val botCommand = StartMessageCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val messageText = welcomeTemplate
            .data("repoUrl", repoUrl)
            .data("donateUrl", donateUrl)
            .render()
        return MessageUtils.createMessage(update, messageText).apply {
            enableHtml(true)
        }
    }
}
