package it.bot.service.impl

import it.bot.model.command.BotCommand
import it.bot.model.command.TestCommand
import it.bot.service.interfaces.CommandParserService
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class TestService : CommandParserService() {

    override val botCommand: BotCommand = TestCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        TODO("Not yet implemented")
    }
}