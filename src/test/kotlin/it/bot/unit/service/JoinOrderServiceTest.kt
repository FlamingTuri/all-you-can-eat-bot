package it.bot.unit.service

import io.quarkus.test.junit.QuarkusTest
import it.bot.model.entity.OrderEntity
import it.bot.model.enum.OrderStatus
import it.bot.model.messages.OrderMessages
import it.bot.repository.OrderRepository
import it.bot.repository.UserRepository
import it.bot.service.impl.JoinOrderService
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
class JoinOrderServiceTest {

    private val botReopenOrderTimeout = 1

    private val orderRepository = Mockito.mock(OrderRepository::class.java)
    private var userRepository = Mockito.mock(UserRepository::class.java)
    private lateinit var joinOrderService: JoinOrderService

    @BeforeAll
    fun setup() {
        joinOrderService = JoinOrderService(botReopenOrderTimeout, orderRepository, userRepository)
    }

    @Test
    fun testJoinNotExistingOrder() {
        Mockito.`when`(orderRepository.findOpenOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(null)

        val orderName = "targetOrderName"
        val update = Update().apply {
            message = Message().apply {
                text = "/joinOrder $orderName"
                chat = Chat().apply {
                    id = 1
                }
            }
        }

        val message = joinOrderService.parseUpdate(update)
        assertEquals(OrderMessages.orderNotFoundError(orderName), message!!.text)
    }

    @Test
    fun testJoinClosedOrder() {
        Mockito.`when`(orderRepository.findOpenOrderWithNameForChat(Mockito.eq(1L), Mockito.anyString()))
            .thenReturn(OrderEntity().apply { status = OrderStatus.Close })

        val orderName = "targetOrderName"
        val update = Update().apply {
            message = Message().apply {
                text = "/joinOrder $orderName"
                chat = Chat().apply {
                    id = 1
                }
            }
        }

        val message = joinOrderService.parseUpdate(update)
        assertEquals(OrderMessages.operationNotAllowedForClosedOrderError(orderName), message!!.text)
    }
}
