package it.bot.service.impl.command

import it.bot.model.command.AddDishesCommand
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import it.bot.util.Regexes
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AddDishesService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    private val addDishService: AddDishService
) : CommandParserService {

    override val botCommand = AddDishesCommand()

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val messageWithoutCommand = destructure(matchResult)
        val responseMessage = messageWithoutCommand.split('\n')
            .asSequence()
            .map { it.trim() }
            .map { convertAndExecuteAddDishCommand(update, it) }
            .joinToString("\n")

        return MessageUtils.createMessage(update, responseMessage)
    }

    private fun destructure(matchResult: MatchResult): String {
        val (_, messageWithoutCommand, _, _, _) = matchResult.destructured
        return messageWithoutCommand.trim()
    }

    private fun convertAndExecuteAddDishCommand(update: Update, i:String): String {
        val command = "${botCommand.addDishCommand.command} $i"
        return when (val matchResult = Regexes.matchMessageWithBotCommand(botCommand.addDishCommand, botUsername, command)) {
            null -> "Error: could not find a match for $command" // should not be possible
            else -> addDishService.executeOperation(update, matchResult).text
        }
    }
}