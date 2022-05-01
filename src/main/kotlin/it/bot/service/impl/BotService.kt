package it.bot.service.impl

import io.quarkus.arc.properties.UnlessBuildProperty
import io.quarkus.runtime.Startup
import it.bot.AllYouCanEatBot
import it.bot.service.interfaces.CommandParserService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


@UnlessBuildProperty(name = "quarkus.profile", stringValue = "test")
@Startup
@ApplicationScoped
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

        botsApi.registerBot(AllYouCanEatBot(botUsername, botToken, commandParserServices))
    }
}
