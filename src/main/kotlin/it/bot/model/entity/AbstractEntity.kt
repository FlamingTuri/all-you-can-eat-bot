package it.bot.model.entity

import java.util.Date
import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Temporal
import javax.persistence.TemporalType

@MappedSuperclass
abstract class AbstractEntity {

    @Column(nullable = false)
    open var active: Boolean = true

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    open var creationDate: Date? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update_date")
    open var lastUpdateDate: Date? = null

    @PrePersist
    fun initCreationDate() {
        creationDate = Date()
        lastUpdateDate = creationDate
    }

    @PreUpdate
    fun updateLastUpdateDate() {
        lastUpdateDate = Date()
    }
}