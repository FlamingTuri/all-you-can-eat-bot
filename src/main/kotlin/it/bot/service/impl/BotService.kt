package it.bot.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import it.bot.AllYouCanEatBot
import it.bot.client.rest.TelegramRestClient
import it.bot.service.interfaces.CommandParserService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@Suppress("unused")
@Startup
@ApplicationScoped
@UnlessBuildProfile("test")
class BotService(
    @ConfigProperty(name = "bot.username") private val botUsername: String,
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @Inject private val startMessageService: StartMessageService,
    @Inject private val helpMessageService: HelpMessageService,
    @Inject private val createOrderService: CreateOrderService,
    @Inject private val joinOrderService: JoinOrderService,
    @Inject private val leaveOrderService: LeaveOrderService,
    @Inject private val closeOrderService: CloseOrderService,
    @Inject private val openOrderService: OpenOrderService,
    @Inject private val showOrderService: ShowOrderService,
    @Inject private val blameDishService: BlameDishService,
    @Inject private val addDishService: AddDishService,
    @Inject private val nameDishService: NameDishService,
    @Inject private val removeDishService: RemoveDishService,
    @Inject @RestClient private val telegramRestClient: TelegramRestClient
) {

    private val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    private val commandParserServices: List<CommandParserService> = listOf(
        startMessageService, helpMessageService,

        createOrderService, joinOrderService, leaveOrderService,
        closeOrderService, openOrderService, showOrderService, blameDishService,

        addDishService, nameDishService, removeDishService
    )

    init {
        helpMessageService.supportedCommands = commandParserServices.map { it.botCommand }

        setBotCommands()

        botsApi.registerBot(AllYouCanEatBot(botUsername, botToken, commandParserServices))
    }

    private fun setBotCommands() {
        val objectMapper = ObjectMapper()

        val botCommands = commandParserServices.map {
            BotCommand().apply {
                command = it.botCommand.command.lowercase()
                description = it.botCommand.description
            }
        }
        val botCommandsToString = objectMapper.writeValueAsString(botCommands)

        if (Log.isDebugEnabled()) {
            Log.debug("bot commands: $botCommandsToString")
        }

        try {
            val success = telegramRestClient.setBotCommands(botToken, botCommandsToString)
            Log.info("bot commands updated: $success")
        } catch (exception: Exception) {
            Log.error("failed to set bot commands: ", exception)
        }
    }
}
