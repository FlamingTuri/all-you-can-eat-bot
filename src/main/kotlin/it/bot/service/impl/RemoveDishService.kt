package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.UserDishEntity
import it.bot.repository.DishRepository
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.DishUtils
import it.bot.util.FormatUtils
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
                userDish == null -> "Dish $dishMenuNumber not found"
                (quantityToRemove == null) || (userDish.quantity!! <= quantityToRemove) -> deleteUserDish(userDish)
                else -> subtractQuantity(userDish, quantityToRemove)
            }

            return MessageUtils.createMessage(update, messageText)
        }
    }

    private fun destructure(matchResult: MatchResult): Pair<Int, Int?> {
        val (_, dishMenuNumber, _, _, dishQuantity) = matchResult.destructured
        return Pair(
            dishMenuNumber.toInt(),
            if (dishQuantity == "") null else dishQuantity.toInt(),
        )
    }

    private fun deleteUserDish(userDish: UserDishEntity): String {
        Log.info("deleting user dish ${userDish.userDishId}")
        userDishRepository.delete(userDish)

        val orderHasDishes = userDishRepository.checkIfOrderHasDishes(userDish)

        if (!orderHasDishes) {
            Log.info("deleting dish ${userDish.dishId} since it does not belong to any user")
            dishRepository.deleteById(userDish.dishId!!)
        }

        return "Successfully removed dish ${userDish.dish?.menuNumber} ${FormatUtils.wrapIfNotNull(userDish.dish?.name)}"
    }

    private fun subtractQuantity(userDish: UserDishEntity, dishQuantityToRemove: Int): String {
        userDish.quantity = userDish.quantity!! - dishQuantityToRemove
        userDishRepository.persist(userDish)
        return "Successfully reduced dish ${DishUtils.formatDishInfo(userDish.dish!!)} " +
                "quantity to ${userDish.quantity}"
    }
}
