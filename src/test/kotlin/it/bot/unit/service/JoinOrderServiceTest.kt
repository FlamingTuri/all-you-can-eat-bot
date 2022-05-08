package it.bot.unit.service

import io.quarkus.test.junit.QuarkusTest
import it.bot.model.entity.OrderEntity
import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.model.messages.OrderMessages
import it.bot.repository.OrderRepository
import it.bot.repository.UserRepository
import it.bot.service.impl.command.JoinOrderService
import it.bot.service.impl.UpdateParserService
import java.util.Calendar
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JoinOrderServiceTest {

    private val botReopenOrderTimeout = 1

    private val updateParserService = UpdateParserService("test-bot")
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

        val message = updateParserService.parseUpdate(joinOrderService, update)
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

        val message = updateParserService.parseUpdate(joinOrderService, update)
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
                        status = OrderStatus.Close
                        lastUpdateDate = current.time
                    }
                })
            )

        val update = Update().apply {
            message = Message().apply {
                text = "/joinOrder $orderName"
                chat = Chat().apply {
                    id = 1
                }
                from = User().apply {
                    id = telegramUserId
                }
            }
        }

        val message = updateParserService.parseUpdate(joinOrderService, update)
        assertEquals(OrderMessages.orderCanBeReopenedError(orderName), message!!.text)
    }
}
