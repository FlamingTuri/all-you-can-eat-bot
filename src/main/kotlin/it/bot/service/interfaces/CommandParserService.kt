package it.bot.service.interfaces

import it.bot.model.command.BotCommand
import it.bot.model.dto.MessageDto
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

interface CommandParserService {

    val botCommand: BotCommand

    fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage?
}
