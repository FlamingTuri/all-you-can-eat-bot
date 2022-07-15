package it.bot.service.impl.command

import io.quarkus.logging.Log
import it.bot.model.command.RemoveDishCommand
import it.bot.model.dto.MessageDto
import it.bot.model.entity.UserDishEntity
import it.bot.model.entity.UserEntity
import it.bot.model.enum.OrderStatus
import it.bot.repository.DishRepository
import it.bot.repository.UserDishRepository
import it.bot.repository.UserRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.DishUtils
import it.bot.util.MessageUtils
import it.bot.util.OrderUtils
import it.bot.util.UserUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RemoveDishService(
    private val userRepository: UserRepository,
    private val dishRepository: DishRepository,
    private val userDishRepository: UserDishRepository
) : CommandParserService {

    override val botCommand = RemoveDishCommand()

    override fun executeOperation(messageDto: MessageDto, matchResult: MatchResult): SendMessage {
        val (dishMenuNumber, quantityToRemove) = destructure(matchResult)

        if ((quantityToRemove != null) && (quantityToRemove <= 0)) {
            Log.error(
                "inserted a wrong quantity value: $quantityToRemove, " +
                        "telegramUserId: ${MessageUtils.getTelegramUserId(messageDto)}, " +
                        "telegramChatId: ${MessageUtils.getChatId(messageDto)}"
            )
            return MessageUtils.createMessage(messageDto, "Error: quantity to remove must be greater than 0")
        }

        val user = userRepository.findUser(MessageUtils.getTelegramUserId(messageDto))
        return when {
            user == null -> UserUtils.getUserDoesNotBelongToOrderMessage(messageDto)
            user.order?.status == OrderStatus.Closed ->
                OrderUtils.getOperationNotAllowedWhenOrderIsClosedMessage(messageDto, user.order?.name!!)
            else -> subtractQuantityOrDeleteDish(messageDto, dishMenuNumber, quantityToRemove, user)
        }
    }

    private fun destructure(matchResult: MatchResult): Pair<Int, Int?> {
        val (_, _, dishMenuNumber, _, _, dishQuantity) = matchResult.destructured
        return Pair(
            dishMenuNumber.toInt(),
            if (dishQuantity == "") null else dishQuantity.toInt(),
        )
    }

    private fun subtractQuantityOrDeleteDish(
        messageDto: MessageDto, dishMenuNumber: Int, quantityToRemove: Int?, user: UserEntity
    ): SendMessage {
        val userDish = userDishRepository.findUserDish(dishMenuNumber, user.telegramUserId!!)

        val messageText = when {
            userDish == null -> "Dish $dishMenuNumber not found"
            (quantityToRemove == null) || (userDish.quantity!! <= quantityToRemove) -> deleteUserDish(userDish)
            else -> subtractQuantity(userDish, quantityToRemove)
        }

        return MessageUtils.createMessage(messageDto, messageText)
    }

    private fun deleteUserDish(userDish: UserDishEntity): String {
        Log.info("deleting user dish ${userDish.userDishId}")
        userDishRepository.delete(userDish)

        val orderHasDishes = userDishRepository.checkIfOrderHasDishes(userDish)

        if (!orderHasDishes) {
            Log.info("deleting dish ${userDish.dishId} since it does not belong to any user")
            dishRepository.deleteById(userDish.dishId!!)
        }

        return "Successfully removed your dish ${DishUtils.formatDishInfo(userDish.dish!!)}"
    }

    private fun subtractQuantity(userDish: UserDishEntity, dishQuantityToRemove: Int): String {
        userDish.quantity = userDish.quantity!! - dishQuantityToRemove
        userDishRepository.persist(userDish)
        return "Successfully reduced your dish ${DishUtils.formatDishInfo(userDish.dish!!)} " +
                "quantity to ${userDish.quantity}"
    }
}
