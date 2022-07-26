package it.bot.service.impl.command

import it.bot.model.command.AddDishesCommand
import it.bot.model.dto.MessageDto
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.Regexes
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AddDishesService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    private val addDishService: AddDishService
) : CommandParserService {

    override val botCommand = AddDishesCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage? {
        val messageWithoutCommand = destructure(matchResult)
        val responseMessage = messageWithoutCommand.split('\n')
            .asSequence()
            .map { it.trim() }
            .map { convertAndExecuteAddDishCommand(messageDto, it) }
            .joinToString("\n")

        return MessageUtils.createMessage(messageDto, responseMessage)
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, messageWithoutCommand, _, _, _) = matchResult.destructured
        return messageWithoutCommand.trim()
    }

    private fun convertAndExecuteAddDishCommand(messageDto: MessageDto, text: String): String {
        val addDishCommand = botCommand.addDishCommand
        val command = "${addDishCommand.command} $text"
        return when (val matchResult = Regexes.matchMessageWithBotCommand(addDishCommand, botUsername, command)) {
            null -> "Error: could not find a match for $command" // should not be possible
            else -> addDishService.executeOperation(messageDto, matchResult).text
        }
    }
}