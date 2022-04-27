package it.bot.util

import it.bot.model.entity.OrderEntity
import java.util.Calendar

object TimeUtils {

    fun hasTimeElapsed(order: OrderEntity, minutes: Int): Boolean {
        val currentTimeNow = Calendar.getInstance()
        currentTimeNow.add(Calendar.MINUTE, -minutes)
        return currentTimeNow.time >= order.lastUpdateDate
    }
}