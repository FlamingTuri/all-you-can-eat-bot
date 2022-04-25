package it.bot.model.dto

data class TelegramUserDto(
    var id: Long? = null,
    var isBot: Boolean = false,
    var firstName: String = "unknown",
    var lastName: String = "unknown",
    var username: String = "unknown",
    var languageCode: String = "en"
)
