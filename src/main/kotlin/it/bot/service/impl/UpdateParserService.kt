package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.command.BotCommand
import it.bot.model.dto.MessageDto
import it.bot.model.entity.CommandCacheEntity
import it.bot.repository.CommandCacheRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.Regexes
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional


@ApplicationScoped
class UpdateParserService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.command.cache.validity.time") private val minutes: Int,
    private val botCommandsService: BotCommandsService,
    private val commandCacheRepository: CommandCacheRepository
) {

    @Transactional
    fun handleUpdate(messageDto: MessageDto): SendMessage? {
        return parseUpdate(getCommandServiceWithCacheFallback(messageDto), messageDto)
    }

    private fun getCommandServiceWithCacheFallback(messageDto: MessageDto): CommandParserService? {
        return getCommandService(messageDto) ?: getCommandServiceUsingCache(messageDto)
    }

    private fun getCommandService(messageDto: MessageDto): CommandParserService? {
        return getCommandServiceForMessage(messageDto.text)
    }

    private fun getCommandServiceUsingCache(messageDto: MessageDto): CommandParserService? {
        val commandCache = getExistingCommandCacheEntity(messageDto)

        return commandCache?.let {
            commandCacheRepository.delete(it)

            val originalMessage = messageDto.text
            val messageWithCommand = "${it.command} $originalMessage"
            Log.info("Found pending command from cache, the message will become: $messageWithCommand")
            getCommandServiceForMessage(messageWithCommand).also {
                messageDto.text = messageWithCommand
            }
        }
    }

    private fun getCommandServiceForMessage(message: String): CommandParserService? {
        return botCommandsService.getCommandServices().find {
            it.botCommand.matches(message, botUsername)
        }
    }

    private fun parseUpdate(commandParserService: CommandParserService?, messageDto: MessageDto): SendMessage? {
        return if (commandParserService == null) {
            val errorMessage = "no command support for '${messageDto.text}'"
            Log.error(errorMessage)
            MessageUtils.createMessage(messageDto, "Error: $errorMessage")
        } else {
            val botCommand = commandParserService.botCommand
            val messageText = messageDto.text
            val matchResult = Regexes.matchMessageWithBotCommand(botCommand, botUsername, messageText)
            return when {
                matchResult != null -> commandParserService.executeOperation(messageDto, matchResult).also {
                    deleteCachedCommand(messageDto)
                }
                botCommand.isExactMatch(messageText, botUsername) -> addCommandToCache(messageDto, botCommand)
                else -> MessageUtils.getInvalidCommandMessage(messageDto, botCommand.command, botCommand.format).also {
                    deleteCachedCommand(messageDto)
                }
            }
        }
    }

    private fun addCommandToCache(messageDto: MessageDto, botCommand: BotCommand): SendMessage {
        val commandCache = when (val existingCommandCache = getExistingCommandCacheEntity(messageDto)) {
            null -> CommandCacheEntity().apply {
                this.chatId = MessageUtils.getChatId(messageDto)
                this.telegramUserId = MessageUtils.getTelegramUserId(messageDto)
                command = botCommand.command
            }
            else -> existingCommandCache.apply {
                command = botCommand.command
            }
        }
        commandCacheRepository.persist(commandCache)

        return getWaitingResponseMessage(messageDto, botCommand)
    }

    private fun getExistingCommandCacheEntity(messageDto: MessageDto): CommandCacheEntity? {
        val chatId = MessageUtils.getChatId(messageDto)
        val telegramUserId = MessageUtils.getTelegramUserId(messageDto)
        return commandCacheRepository.findChatUserCommand(chatId, telegramUserId, minutes)
    }

    private fun getWaitingResponseMessage(messageDto: MessageDto, botCommand: BotCommand): SendMessage {
        val messageText = getWaitingResponseMessageText(botCommand)
        return MessageUtils.createMessage(messageDto, messageText).apply {
            if (messageDto.chat.isGroupChat || messageDto.chat.isSuperGroupChat) {
                setReplyMarkup(this, messageDto)
            }
        }
    }

    private fun getWaitingResponseMessageText(botCommand: BotCommand): String {
        val command = botCommand.command
        val format = botCommand.format
        val isMultiline = botCommand.isMultiline()
        return "Finalize $command operation by replying with a message with the following format: $format" +
                if (isMultiline) "\nThe format can be repeated over multiple lines to make bulk operations" else ""
    }

    private fun setReplyMarkup(sendMessage: SendMessage, messageDto: MessageDto) {
        sendMessage.apply {
            replyToMessageId = messageDto.messageId
            replyMarkup = ForceReplyKeyboard().apply {
                forceReply = true
                selective = true
            }
        }
    }

    private fun deleteCachedCommand(messageDto: MessageDto) {
        val chatId = MessageUtils.getChatId(messageDto)
        val telegramUserId = MessageUtils.getTelegramUserId(messageDto)
        commandCacheRepository.deleteChatUserCommand(chatId, telegramUserId)
    }
}
