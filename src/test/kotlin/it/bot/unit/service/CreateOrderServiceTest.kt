package it.bot.unit.service

import io.quarkus.test.junit.QuarkusTest
import it.bot.model.dto.MessageDto
import it.bot.model.entity.OrderEntity
import it.bot.model.messages.OrderMessages
import it.bot.repository.CommandCacheRepository
import it.bot.repository.OrderRepository
import it.bot.service.impl.BotCommandsService
import it.bot.service.impl.UpdateParserService
import it.bot.service.impl.command.CreateOrderService
import it.bot.unit.util.MockitoUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import kotlin.test.assertEquals

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateOrderServiceTest {

    private val orderRepository = Mockito.mock(OrderRepository::class.java)
    private val commandCacheRepository = Mockito.mock(CommandCacheRepository::class.java)
    private lateinit var botCommandsService: BotCommandsService
    private lateinit var createOrderService: CreateOrderService
    private lateinit var updateParserService: UpdateParserService

    @BeforeAll
    fun setup() {
        botCommandsService = Mockito.mock(BotCommandsService::class.java)
        createOrderService = CreateOrderService(orderRepository)

        updateParserService = UpdateParserService(
            "test-bot", 5, botCommandsService, commandCacheRepository
        )

        Mockito.`when`(botCommandsService.getCommandServices()).thenReturn(listOf(createOrderService))
    }

    @Test
    fun testOrderCreation() {
        Mockito.`when`(orderRepository.existsOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(false)

        Mockito.doNothing().`when`(orderRepository).persist(MockitoUtils.any(OrderEntity::class.java))

        val orderName = "newOrderName"
        val messageDto = MessageDto(
            10000,
            User().apply {
                id = 100
            },
            "/createOrder $orderName",
            Chat().apply {
                id = 1
            })

        val message = updateParserService.handleUpdate(messageDto)
        assertEquals(OrderMessages.orderCreationSuccessful(orderName), message!!.text)
    }

    @Test
    fun testOrderAlreadyExistingForChat() {
        Mockito.`when`(orderRepository.existsOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(true)

        val messageDto = MessageDto(
            10000,
            User().apply {
                id = 100
            },
            "/createOrder existingOrderName",
            Chat().apply {
                id = 1
            })

        val message = updateParserService.handleUpdate(messageDto)
        assertEquals(OrderMessages.orderWithTheSameNameError, message!!.text)
    }
}
