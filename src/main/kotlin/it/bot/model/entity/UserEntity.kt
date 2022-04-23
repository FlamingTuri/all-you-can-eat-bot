package it.bot.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

private const val TABLE = "user"
private const val SEQUENCE = "${TABLE}_sequence"
private const val GENERATOR = "${TABLE}_generator"

@Entity
@Table(name = TABLE)
@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE, allocationSize = 1)
class UserEntity : AbstractEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @Column(name = "user_id", nullable = false)
    var userId: Long? = null

    @Column(name = "telegram_user_id", nullable = false)
    var telegramUserId: Long? = null

    @Column(name = "order_id", nullable = false, insertable = false, updatable = false)
    var orderId: Long? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", /*referencedColumnName = "order_id",*/ nullable = false)
    var order: Order? = null
}
