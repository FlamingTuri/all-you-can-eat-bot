package it.bot.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

private const val TABLE = "dish"
private const val SEQUENCE = "${TABLE}_sequence"
private const val GENERATOR = "${TABLE}_generator"

@Entity
@Table(name = TABLE)
@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE, allocationSize = 1)
class DishEntity : AbstractEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @Column(name = "dish_id", nullable = false)
    var dishId: Long? = null

    @Column(name = "menu_number", nullable = false)
    var menuNumber: Int? = null

    @Column
    var name: String? = null

    @Column(name = "order_id", nullable = false, insertable = false, updatable = false)
    var orderId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: OrderEntity? = null
}
