package it.bot.model.dto

data class TelegramUserDto(
    var id: Long? = null,
    var isBot: Boolean? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var username: String? = null,
    var languageCode: String? = null
)
