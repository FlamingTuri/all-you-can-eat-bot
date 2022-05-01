package it.bot.model.command

interface BotCommand {

    val command: String

    val pattern: String

    val format: String

    val description: String

    val commandType: CommandType
}
