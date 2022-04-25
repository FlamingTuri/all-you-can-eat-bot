package it.bot.util

import it.bot.model.entity.DishEntity

object DishUtils {

    fun formatDishInfo(dish: DishEntity): String {
        return dish.name?.let { "${dish.menuNumber} ($it)" } ?: "${dish.menuNumber}"
    }
}
