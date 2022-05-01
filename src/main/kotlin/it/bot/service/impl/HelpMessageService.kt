package it.bot.service.impl

import it.bot.model.command.BotCommand
import it.bot.model.command.HelpMessageCommand
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class HelpMessageService : CommandParserService() {

    override val botCommand = HelpMessageCommand()

    var supportedCommands: List<BotCommand> = listOf()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val commandsToString = supportedCommands.joinToString("\n") {
            "${it.command} ${it.format}\n    - ${it.description}"
        }
        val messageText = "Command supported by all you can eat bot:\n$commandsToString"

        return MessageUtils.createMessage(update, messageText)
    }
}