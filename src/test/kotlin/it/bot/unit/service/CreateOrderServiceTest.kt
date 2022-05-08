package it.bot.unit.service

import io.quarkus.test.junit.QuarkusTest
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
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import kotlin.test.assertEquals

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateOrderServiceTest {

    private val orderRepository = Mockito.mock(OrderRepository::class.java)
    private val botCommandsService = Mockito.mock(BotCommandsService::class.java)
    private val commandCacheRepository = Mockito.mock(CommandCacheRepository::class.java)
    private lateinit var createOrderService: CreateOrderService
    private val updateParserService = UpdateParserService(
        "test-bot", botCommandsService, commandCacheRepository
    )

    @BeforeAll
    fun setup() {
        createOrderService = CreateOrderService(orderRepository)
        Mockito.`when`(botCommandsService.getCommandServices()).thenReturn(listOf(createOrderService))
    }

    @Test
    fun testOrderCreation() {
        Mockito.`when`(orderRepository.existsOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(false)

        Mockito.doNothing().`when`(orderRepository).persist(MockitoUtils.any(OrderEntity::class.java))

        val orderName = "newOrderName"
        val update = Update().apply {
            message = Message().apply {
                text = "/createOrder $orderName"
                chat = Chat().apply {
                    id = 1
                }
                from = User().apply {
                    id = 100
                }
            }
        }

        val message = updateParserService.handleUpdate(update)
        assertEquals(OrderMessages.orderCreationSuccessful(orderName), message!!.text)
    }

    @Test
    fun testOrderAlreadyExistingForChat() {
        Mockito.`when`(orderRepository.existsOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(true)

        val update = Update().apply {
            message = Message().apply {
                text = "/createOrder existingOrderName"
                chat = Chat().apply {
                    id = 1
                }
                from = User().apply {
                    id = 100
                }
            }
        }

        val message = updateParserService.handleUpdate(update)
        assertEquals(OrderMessages.orderWithTheSameNameError, message!!.text)
    }
}
