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
import it.bot.util.UserUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class RemoveDishService(
    @Inject private val userRepository: UserRepository,
    @Inject private val dishRepository: DishRepository,
    @Inject private val userDishRepository: UserDishRepository
) : CommandParserService() {

    override val command: String = "/removeDish"

    override val commandPattern: String = "(\\s*)(\\d+)((\\s+)(\\d+))?\\s*"

    override val commandFormat: String = "{menuNumber} {quantityToRemove:all}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val (dishMenuNumber, quantityToRemove) = destructure(matchResult)

        if ((quantityToRemove != null) && (quantityToRemove <= 0)) {
            Log.error(
                "inserted a wrong quantity value: $quantityToRemove, " +
                        "telegramUserId: ${MessageUtils.getTelegramUserId(update)}, " +
                        "telegramChatId: ${MessageUtils.getChatId(update)}"
            )
            return MessageUtils.createMessage(update, "Error: quantity to remove must be greater than 0")
        }

        val user = userRepository.findUser(MessageUtils.getTelegramUserId(update))
        return if (user == null) {
            UserUtils.getUserDoesNotBelongToOrderMessage(update)
        } else {
            val userDish = userDishRepository.findUserDish(dishMenuNumber, user.telegramUserId!!)

            val messageText = when {
                userDish == null -> "TODO"
                (quantityToRemove == null) || (userDish.quantity!! <= quantityToRemove) -> deleteUserDish(userDish)
                else -> subtractQuantity(userDish, quantityToRemove)
            }

            return MessageUtils.createMessage(update, messageText)
        }
    }

    private fun deleteUserDish(userDish: UserDishEntity): String {
        userDishRepository.delete(userDish)
        return "TODO"
    }

    private fun subtractQuantity(userDish: UserDishEntity, dishQuantityToRemove: Int): String {
        userDish.quantity = userDish.quantity!! - dishQuantityToRemove
        userDishRepository.persist(userDish)
        return "TODO"
    }

    private fun destructure(matchResult: MatchResult): Pair<Int, Int?> {
        val (_, dishMenuNumber, _, _, dishQuantity) = matchResult.destructured
        return Pair(
            dishMenuNumber.toInt(),
            if (dishQuantity == "") null else dishQuantity.toInt(),
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
