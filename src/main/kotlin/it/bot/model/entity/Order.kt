package it.bot.model.entity

import it.bot.model.enum.OrderStatus
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "order")
class Order : AbstractEntity() {
    @Id
    @Column(name = "order_id", nullable = false)
    var orderId: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.Open

    @Column(name = "chat_id", nullable = false)
    var chatId: Long? = null
}