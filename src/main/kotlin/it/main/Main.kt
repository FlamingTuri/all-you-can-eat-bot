package it.main

import it.bot.AllYouCanEatBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    // Instantiate Telegram Bots API
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    // Register our bot
    try {
        botsApi.registerBot(AllYouCanEatBot("todo"))
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}