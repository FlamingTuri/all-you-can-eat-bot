package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class UpdateParserService {

    @Transactional
    fun parseUpdate(commandParserService: CommandParserService?, update: Update): SendMessage? {
        return if (commandParserService == null) {
            Log.error("no command support for '${MessageUtils.getChatMessage(update)}'")
            null
        } else {
            val botCommand = commandParserService.botCommand
            val regex = "(?i)${botCommand.command}(?-i)${botCommand.pattern}".toRegex()
            when (val matchResult = regex.matchEntire(update.message.text)) {
                null -> MessageUtils.getInvalidCommandMessage(update, botCommand.command, botCommand.format)
                else -> commandParserService.executeOperation(update, matchResult)
            }
        }
    }
}
