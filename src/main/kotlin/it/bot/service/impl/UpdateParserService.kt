package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.command.BotCommand
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class UpdateParserService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @Inject private val botCommandsService: BotCommandsService
) {

    @Transactional
    fun handleUpdate(update: Update): SendMessage? {
        return parseUpdate(getCommandService(update), update)
    }

    private fun getCommandService(update: Update): CommandParserService? {
        return botCommandsService.getCommandServices().find {
            it.botCommand.matches(MessageUtils.getChatMessage(update), botUsername)
        }
    }

    private fun parseUpdate(commandParserService: CommandParserService?, update: Update): SendMessage? {
        return if (commandParserService == null) {
            Log.error("no command support for '${MessageUtils.getChatMessage(update)}'")
            null
        } else {
            val botCommand = commandParserService.botCommand
            val regex = "(?i)${botCommand.command}(?-i)${botCommand.pattern}".toRegex()
            val messageText = MessageUtils.getChatMessage(update)
            val matchResult = regex.matchEntire(messageText)
            return when {
                matchResult != null -> commandParserService.executeOperation(update, matchResult)
                botCommand.isExactMatch(messageText, botUsername) -> getWaitingResponseMessage(update, botCommand)
                else -> MessageUtils.getInvalidCommandMessage(update, botCommand.command, botCommand.format)
            }
        }
    }

    private fun getWaitingResponseMessage(update: Update, botCommand: BotCommand): SendMessage {
        val messageText = "Waiting for response: ${botCommand.format}"
        return MessageUtils.createMessage(update, messageText).apply {
            replyMarkup = ForceReplyKeyboard().apply {
                replyToMessageId = update.message.messageId
                forceReply = true
                selective = true
            }
        }
    }
}
