package it.bot.util

object FormatUtils {

    fun wrapIfNotNull(string: String?): String = if (string == null || string == "") "" else "($string)"

    fun tagUsername(string: String?): String = string?.let { "@$it" } ?: "unknown"
}