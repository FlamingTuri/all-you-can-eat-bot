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

private const val TABLE = "user_dish"
private const val SEQUENCE = "${TABLE}_sequence"
private const val GENERATOR = "${TABLE}_generator"

@Entity
@Table(name = TABLE)
@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE, allocationSize = 1)
class UserDishEntity : AbstractEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @Column(name = "user_dish_id", nullable = false)
    var userDishId: Long? = null

    @Column(name = "quantity", nullable = false)
    var quantity: Int? = null

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    var userId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null

    @Column(name = "dish_id", nullable = false, insertable = false, updatable = false)
    var dishId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    var dish: DishEntity? = null
}
