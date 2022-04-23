package it.bot.service.interfaces

import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

abstract class CommandParserService() {

    abstract val command: String

    protected abstract val commandPattern: String

    open fun parseUpdate(update: Update): SendMessage? {
        val regex = "$command $commandPattern".toRegex()
        return when (val matchResult = regex.matchEntire(update.message.text)) {
            null -> MessageUtils.getInvalidCommandMessage(update, command)
            else -> executeOperation(update, matchResult)
        }
    }

    protected abstract fun executeOperation(update: Update, matchResult: MatchResult): SendMessage?
}
