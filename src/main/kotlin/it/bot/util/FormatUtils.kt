package it.bot.util

object FormatUtils {

    fun wrapIfNotNull(string: String?) = if (string == null || string == "") "" else "($string)"
}