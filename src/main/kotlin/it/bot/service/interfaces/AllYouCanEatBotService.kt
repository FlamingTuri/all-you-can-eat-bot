package it.bot.service.interfaces

import org.telegram.telegrambots.meta.api.objects.Update

interface AllYouCanEatBotService {

    fun handleUpdate(update: Update)
}
