package it.bot.service.impl

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import it.bot.model.command.StartMessageCommand
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class StartMessageService(
    @ConfigProperty(name = "bot.repo.url", defaultValue = "") val repoUrl: String,
    @ConfigProperty(name = "bot.donate.url", defaultValue = "") val donateUrl: String,
    @Location("welcome.html") val welcomeTemplate: Template
) : CommandParserService() {

    override val botCommand = StartMessageCommand()

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

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
