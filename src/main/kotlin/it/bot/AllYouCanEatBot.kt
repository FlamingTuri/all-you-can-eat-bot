package it.bot

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.BotOptions
import org.telegram.telegrambots.meta.generics.LongPollingBot


class AllYouCanEatBot(botToken :String) : LongPollingBot {


    override fun onUpdateReceived(update: Update) {}

    override fun getOptions(): BotOptions? {
        return null
    }

    override fun clearWebhook() {}

    override fun getBotUsername(): String {
        return "AllYouCanEatBot"
    }

    override fun getBotToken(): String {
        return botToken
    }
}
