package it.bot.util

import it.bot.model.command.BotCommand

object Regexes {

    const val namePattern = "((\\w+)((\\s+)(\\w+))*)"

    fun matchMessageWithBotCommand(botCommand: BotCommand, botUsername:String, messageText: String): MatchResult? {
        val regex = "(?i)${botCommand.command}(@$botUsername)?(?-i)${botCommand.pattern}".toRegex()
        return regex.matchEntire(messageText)
    }
}
