package it.bot.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table


private const val BASE = "command_cache"
private const val TABLE = "${BASE}_table"
private const val SEQUENCE = "${BASE}_sequence"
private const val GENERATOR = "${BASE}_generator"

@Entity
@Table(name = TABLE)
@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE, allocationSize = 1)
class CommandCacheEntity : AbstractEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @Column(name = "command_cache_id", nullable = false)
    var commandCacheId: Long? = null

    @Column(name = "chat_id", nullable = false)
    var chatId: Long? = null

    @Column(name = "telegram_user_id", nullable = false)
    var telegramUserId: Long? = null

    @Column(name = "command", nullable = false)
    var command: String? = null
}
