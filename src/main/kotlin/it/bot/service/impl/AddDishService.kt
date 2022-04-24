package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.DishEntity
import it.bot.model.entity.UserDishEntity
import it.bot.model.entity.UserEntity
import it.bot.repository.DishRepository
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class AddDishService(
    @Inject private val userRepository: UserRepository,
    @Inject private val dishRepository: DishRepository,
    @Inject private val userDishRepository: UserDishRepository
) : CommandParserService() {

    override val command: String = "/addDish"

    override val commandPattern: String = "(\\s*)(\\d+)((\\s+)(\\d+))?((\\s+)(\\w+))?"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val (dishMenuNumber, dishQuantity, dishName) = destructure(matchResult)

        if (dishQuantity <= 0) {
            Log.error(
                "inserted a wrong quantity value: $dishQuantity, " +
                        "telegramUserId: ${MessageUtils.getTelegramUserId(update)}, " +
                        "telegramChatId: ${MessageUtils.getChatId(update)}"
            )
            return MessageUtils.createMessage(update, "Error quantity must be greater than 0")
        }

        val user = userRepository.findUser(MessageUtils.getTelegramUserId(update))
        if (user == null) {
            Log.error("user ${MessageUtils.getTelegramUserId(update)} not found")
            return MessageUtils.createMessage(
                update,
                "Error: you are not part of an order. Use /joinOrder command before adding a dish"
            )
        }

        val dish = createOrUpdateDish(user, dishMenuNumber, dishName)

        val userDish = addToUserDishes(user, dish, dishQuantity)

        return MessageUtils.createMessage(
            update,
            "Successfully add number ${formatDishInfo(dish)} to order '${user.order!!.name}' " +
                    "(your quantity: ${userDish.quantity})"
        )
    }

    private fun destructure(matchResult: MatchResult): Triple<Int, Int, String?> {
        val (_, dishMenuNumber, _, _, dishQuantity, _, _, dishName) = matchResult.destructured
        return Triple(
            dishMenuNumber.toInt(),
            if (dishQuantity == "") 1 else dishQuantity.toInt(),
            if (dishName == "") null else dishName
        )
    }

    private fun createOrUpdateDish(user: UserEntity, dishMenuNumber: Int, dishName: String?): DishEntity {
        val existingDish = dishRepository.findDish(dishMenuNumber, user.orderId!!)

        val dish = existingDish?.apply {
            dishName?.let {
                Log.info("order ${user.orderId}: updating dish $menuNumber - $it")
                name = it
                dishRepository.persist(existingDish)
            }
        } ?: createDish(user, dishMenuNumber, dishName).also {
            dishRepository.persist(it)
        }

        return dish
    }

    private fun createDish(user: UserEntity, dishMenuNumber: Int, dishName: String?): DishEntity {
        Log.info("order ${user.orderId}: adding dish $dishMenuNumber - $dishName")
        return DishEntity().apply {
            menuNumber = dishMenuNumber
            name = dishName
            order = user.order
        }
    }

    private fun addToUserDishes(user: UserEntity, dish: DishEntity, dishQuantity: Int): UserDishEntity {
        val existingUserDish = userDishRepository.findUserDish(user, dish)
        return if (existingUserDish == null) {
            createUserDish(user, dish, dishQuantity)
        } else {
            existingUserDish.quantity = existingUserDish.quantity?.plus(dishQuantity)
            userDishRepository.persist(existingUserDish)
            existingUserDish
        }
    }

    private fun createUserDish(user: UserEntity, dish: DishEntity, dishQuantity: Int): UserDishEntity {
        return UserDishEntity().apply {
            quantity = dishQuantity
            this.user = user
            this.dish = dish
        }.also {
            userDishRepository.persist(it)
        }
    }

    private fun formatDishInfo(dish: DishEntity): String {
        return dish.name?.let { "${dish.menuNumber} ($it)" } ?: "$dish.menuNumber"
    }
}
