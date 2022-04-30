package it.bot.unit.service

import io.quarkus.test.junit.QuarkusTest
import it.bot.model.entity.OrderEntity
import it.bot.model.messages.OrderMessages
import it.bot.repository.OrderRepository
import it.bot.service.impl.CreateOrderService
import it.bot.unit.util.MockitoUtils
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateOrderServiceTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var createOrderService: CreateOrderService

    @BeforeAll
    fun setup() {
        orderRepository = Mockito.mock(OrderRepository::class.java)
        createOrderService = CreateOrderService(orderRepository)
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
            }
        }

        val message = createOrderService.parseUpdate(update)
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
            }
        }

        val message = createOrderService.parseUpdate(update)
        assertEquals(OrderMessages.orderWithTheSameNameError, message!!.text)
    }
}
