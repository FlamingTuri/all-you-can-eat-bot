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
import javax.inject.Inject


@ApplicationScoped
class BotCommandsService(
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @Inject @RestClient private val telegramRestClient: TelegramRestClient,
    @Inject private val startMessageService: StartMessageService,
    @Inject private val helpMessageService: HelpMessageService,
    @Inject private val chatOrdersService: ChatOrdersService,
    @Inject private val createOrderService: CreateOrderService,
    @Inject private val joinOrderService: JoinOrderService,
    @Inject private val leaveOrderService: LeaveOrderService,
    @Inject private val closeOrderService: CloseOrderService,
    @Inject private val openOrderService: OpenOrderService,
    @Inject private val showOrderService: ShowOrderService,
    @Inject private val blameDishService: BlameDishService,
    @Inject private val addDishService: AddDishService,
    @Inject private val addDishesService: AddDishesService,
    @Inject private val nameDishService: NameDishService,
    @Inject private val removeDishService: RemoveDishService,
    @Inject private val myOrdersService: MyOrdersService
) {

    init {
        helpMessageService.supportedCommands = getCommandServices().map { it.botCommand }
    }

    fun getCommandServices(): List<CommandParserService> {
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
