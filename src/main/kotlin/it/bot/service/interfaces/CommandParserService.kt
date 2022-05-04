package it.bot.service.interfaces

import it.bot.model.command.BotCommand
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.transaction.Transactional

abstract class CommandParserService {

    abstract val botCommand: BotCommand

    @Transactional
    open fun parseUpdate(update: Update): SendMessage? {
        val regex = "(?i)${botCommand.command}(?-i)${botCommand.pattern}".toRegex()
        return when (val matchResult = regex.matchEntire(update.message.text)) {
            null -> MessageUtils.getInvalidCommandMessage(update, botCommand.command, botCommand.format)
            else -> executeOperation(update, matchResult)
        }
    }

    protected abstract fun executeOperation(update: Update, matchResult: MatchResult): SendMessage?
}
