package it.bot.service.impl.command

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import it.bot.model.command.StartMessageCommand
import it.bot.model.dto.MessageDto
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StartMessageService(
    @ConfigProperty(name = "bot.repo.url", defaultValue = "") val repoUrl: String,
    @ConfigProperty(name = "bot.donate.url", defaultValue = "") val donateUrl: String,
    @Location("welcome.html") val welcomeTemplate: Template
) : CommandParserService {

    override val botCommand = StartMessageCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val messageText = welcomeTemplate
            .data("repoUrl", repoUrl)
            .data("donateUrl", donateUrl)
            .render()
        return MessageUtils.createMessage(messageDto, messageText).apply {
            enableHtml(true)
        }
    }
}
