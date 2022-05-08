package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.command.BotCommand
import it.bot.model.entity.CommandCacheEntity
import it.bot.repository.CommandCacheRepository
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
    @Inject private val botCommandsService: BotCommandsService,
    @Inject private val commandCacheRepository: CommandCacheRepository
) {

    @Transactional
    fun handleUpdate(update: Update): SendMessage? {
        return parseUpdate(getCommandServiceWithCacheFallback(update), update)
    }

    private fun getCommandServiceWithCacheFallback(update: Update): CommandParserService? {
        return getCommandService(update) ?: getCommandServiceUsingCache(update)
    }

    private fun getCommandService(update: Update): CommandParserService? {
        val message = MessageUtils.getChatMessage(update)
        return getCommandServiceForMessage(message)
    }

    private fun getCommandServiceUsingCache(update: Update): CommandParserService? {
        val chatId = MessageUtils.getChatId(update)
        val telegramUserId = MessageUtils.getTelegramUserId(update)
        val commandCache = commandCacheRepository.findChatUserCommand(chatId, telegramUserId)

        return commandCache?.let {
            commandCacheRepository.delete(it)

            val originalMessage = MessageUtils.getChatMessage(update)
            val messageWithCommand = "${it.command} $originalMessage"
            Log.info("Found pending command from cache, the message will become: $messageWithCommand")
            getCommandServiceForMessage(messageWithCommand).also {
                update.message.text = messageWithCommand
            }
        }
    }

    private fun getCommandServiceForMessage(message: String): CommandParserService? {
        return botCommandsService.getCommandServices().find {
            it.botCommand.matches(message, botUsername)
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
                matchResult != null -> commandParserService.executeOperation(update, matchResult).also {
                    deleteCachedCommand(update)
                }
                botCommand.isExactMatch(messageText, botUsername) -> addCommandToCache(update, botCommand)
                else -> MessageUtils.getInvalidCommandMessage(update, botCommand.command, botCommand.format).also {
                    deleteCachedCommand(update)
                }
            }
        }
    }

    private fun addCommandToCache(update: Update, botCommand: BotCommand): SendMessage {
        val commandCache = CommandCacheEntity().apply {
            chatId = MessageUtils.getChatId(update)
            telegramUserId = MessageUtils.getChatId(update)
            command = botCommand.command
        }
        commandCacheRepository.persist(commandCache)

        return getWaitingResponseMessage(update, botCommand)
    }

    private fun getWaitingResponseMessage(update: Update, botCommand: BotCommand): SendMessage {
        val messageText = "Waiting for response: ${botCommand.format}"
        return MessageUtils.createMessage(update, messageText).apply {
            if (update.message.isGroupMessage) {
                setReplyMarkupForGroups(this, update)
            }
        }
    }

    private fun setReplyMarkupForGroups(sendMessage: SendMessage, update: Update) {
        sendMessage.apply {
            replyToMessageId = update.message.messageId
            replyMarkup = ForceReplyKeyboard().apply {
                forceReply = true
                selective = true
            }
        }
    }

    private fun deleteCachedCommand(update: Update) {
        val chatId = MessageUtils.getChatId(update)
        val telegramUserId = MessageUtils.getTelegramUserId(update)
        commandCacheRepository.deleteChatUserCommand(chatId, telegramUserId)
    }
}
