package it.bot.service.interfaces

import it.bot.model.command.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

interface CommandParserService {

    val botCommand: BotCommand

    fun executeOperation(update: Update, matchResult: MatchResult): SendMessage?
}
