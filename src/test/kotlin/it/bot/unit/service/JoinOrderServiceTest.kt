package it.bot.unit.service

import io.quarkus.test.junit.QuarkusTest
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.model.messages.OrderMessages
import it.bot.repository.CommandCacheRepository
import it.bot.repository.OrderRepository
import it.bot.repository.UserRepository
import it.bot.service.impl.BotCommandsService
import it.bot.service.impl.UpdateParserService
import it.bot.service.impl.command.JoinOrderService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import java.util.Calendar
import kotlin.test.assertEquals

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JoinOrderServiceTest {

    private val botReopenOrderTimeout = 1

    private val orderRepository = Mockito.mock(OrderRepository::class.java)
    private var userRepository = Mockito.mock(UserRepository::class.java)
    private val commandCacheRepository = Mockito.mock(CommandCacheRepository::class.java)
    private lateinit var joinOrderService: JoinOrderService
    private lateinit var botCommandsService: BotCommandsService
    private lateinit var updateParserService: UpdateParserService

    @BeforeAll
    fun setup() {
        joinOrderService = JoinOrderService(botReopenOrderTimeout, orderRepository, userRepository)
        botCommandsService = Mockito.mock(BotCommandsService::class.java)

        updateParserService = UpdateParserService(
            "test-bot", 5, botCommandsService, commandCacheRepository
        )

        Mockito.`when`(botCommandsService.getCommandServices()).thenReturn(listOf(joinOrderService))
    }

    @Test
    fun testJoinNotExistingOrder() {
        Mockito.`when`(orderRepository.findOpenOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(null)

        val orderName = "targetOrderName"
        val messageDto = MessageDto(
            10000,
            User().apply {
                id = 100
            },
            "/joinOrder $orderName",
            Chat().apply {
                id = 1
            })

        val message = updateParserService.handleUpdate(messageDto)
        assertEquals(OrderMessages.orderNotFoundError(orderName), message!!.text)
    }

    @Test
    fun testJoinClosedOrder() {
        Mockito.`when`(orderRepository.findOpenOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(OrderEntity().apply { status = OrderStatus.Closed })

        val orderName = "targetOrderName"
        val messageDto = MessageDto(
            10000,
            User().apply {
                id = 100
            },
            "/joinOrder $orderName",
            Chat().apply {
                id = 1
            })

        val message = updateParserService.handleUpdate(messageDto)
        assertEquals(OrderMessages.operationNotAllowedForClosedOrderError(orderName), message!!.text)
    }

    @Test
    fun testUserClosedOrderCanBeReopened() {
        val telegramUserId = 999999999L
        val orderName = "targetOrderName"

        Mockito.`when`(orderRepository.findOpenOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(OrderEntity().apply { status = OrderStatus.Open })

        val current = Calendar.getInstance()
        Mockito.`when`(userRepository.findUsers(telegramUserId))
            .thenReturn(
                listOf(UserEntity().apply {
                    order = OrderEntity().apply {
                        name = orderName
                        status = OrderStatus.Closed
                        lastUpdateDate = current.time
                    }
                })
            )

        val messageDto = MessageDto(
            10000,
            User().apply {
                id = telegramUserId
            },
            "/joinOrder $orderName",
            Chat().apply {
                id = 1
            })

        val message = updateParserService.handleUpdate(messageDto)
        assertEquals(OrderMessages.orderCanBeReopenedError(orderName), message!!.text)
    }
}
