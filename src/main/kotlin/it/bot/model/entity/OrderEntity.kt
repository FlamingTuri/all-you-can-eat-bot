package it.bot.model.entity

import it.bot.model.enum.OrderStatus
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table


private const val BASE = "order"
private const val TABLE = "${BASE}_table"
private const val SEQUENCE = "${BASE}_sequence"
private const val GENERATOR = "${BASE}_generator"

@Entity
@Table(name = TABLE)
@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE, allocationSize = 1)
class OrderEntity : AbstractEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @Column(name = "order_id", nullable = false)
    var orderId: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.Open

    @Column(name = "chat_id", nullable = false)
    var chatId: Long? = null
}
