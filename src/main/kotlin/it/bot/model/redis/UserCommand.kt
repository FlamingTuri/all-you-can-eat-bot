package it.bot.model.redis

import it.bot.util.MessageUtils
import org.telegram.telegrambots.meta.api.objects.Update

class UserCommand(update: Update) {

    val key: String = getKey(update)
    val value: String = MessageUtils.getChatMessage(update).trim()

    private fun getKey(update: Update): String {
        return "${MessageUtils.getChatId(update)}:${MessageUtils.getTelegramUserId(update)}"
    }
}
