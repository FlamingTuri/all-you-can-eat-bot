package it.bot.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.logging.Log
import it.bot.client.rest.TelegramRestClient
import it.bot.service.impl.command.AddDishService
import it.bot.service.impl.command.AddDishesService
import it.bot.service.impl.command.BlameDishService
import it.bot.service.impl.command.ChatOrdersService
import it.bot.service.impl.command.CloseOrderService
import it.bot.service.impl.command.CreateOrderService
import it.bot.service.impl.command.HelpMessageService
import it.bot.service.impl.command.JoinOrderService
import it.bot.service.impl.command.LeaveOrderService
import it.bot.service.impl.command.MyOrdersService
import it.bot.service.impl.command.NameDishService
import it.bot.service.impl.command.OpenOrderService
import it.bot.service.impl.command.RemoveDishService
import it.bot.service.impl.command.ShowOrderService
import it.bot.service.impl.command.StartMessageService
import it.bot.service.interfaces.CommandParserService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class BotCommandsService(
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @RestClient private val telegramRestClient: TelegramRestClient,
    private val startMessageService: StartMessageService,
    private val helpMessageService: HelpMessageService,
    private val chatOrdersService: ChatOrdersService,
    private val createOrderService: CreateOrderService,
    private val joinOrderService: JoinOrderService,
    private val leaveOrderService: LeaveOrderService,
    private val closeOrderService: CloseOrderService,
    private val openOrderService: OpenOrderService,
    private val showOrderService: ShowOrderService,
    private val blameDishService: BlameDishService,
    private val addDishService: AddDishService,
    private val addDishesService: AddDishesService,
    private val nameDishService: NameDishService,
    private val removeDishService: RemoveDishService,
    private val myOrdersService: MyOrdersService
) {

    init {
        helpMessageService.supportedCommands = getCommandServices().map { it.botCommand }
    }

    final fun getCommandServices(): List<CommandParserService> {
        return listOf(
            startMessageService, helpMessageService,

            addDishService, addDishesService, nameDishService, removeDishService, myOrdersService,

            chatOrdersService, createOrderService, joinOrderService, leaveOrderService,
            closeOrderService, openOrderService, showOrderService, blameDishService
        )
    }

    fun setBotSupportedCommands() {
        val objectMapper = ObjectMapper()

        val botCommands = getCommandServices().map {
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
