package it.bot.unit.util

import org.mockito.Mockito

object MockitoUtils {

    /**
     * Fix for mockito error "any(**class**) must not be null"
     */
    fun <T> any(type: Class<T>): T = Mockito.any(type)
}
