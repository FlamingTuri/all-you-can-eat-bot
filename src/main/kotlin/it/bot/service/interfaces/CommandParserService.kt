package it.bot.service.interfaces

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

interface CommandParserService {

    fun getCommand(): String

    fun parseUpdate(update: Update): SendMessage?
}