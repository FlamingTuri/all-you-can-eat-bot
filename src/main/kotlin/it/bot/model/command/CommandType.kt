package it.bot.model.command

enum class CommandType(val description: String) {
    Info("Bot info commands"),
    Group("Group commands, should be used all in the same group to work"),
    Anywhere("Unrestricted commands, can be used directly in the bot chat to avoid flooding groups with messages");
}
