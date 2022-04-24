package it.bot.service.impl

import io.quarkus.logging.Log
import it.bot.model.entity.DishEntity
import it.bot.model.entity.UserEntity
import it.bot.repository.DishRepository
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
    @Inject private val dishRepository: DishRepository
) : CommandParserService() {

    override val command: String = "/addDish"

    override val commandPattern: String = "(\\s*)(\\d+)((\\s+)(\\d+))?((\\s+)(\\w+))?"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val (_, dishMenuNumber, _, _, dishQuantity, _, _, dishName) = matchResult.destructured

        val user = userRepository.findUser(MessageUtils.getTelegramUserId(update))
        if (user == null) {
            // TODO
            Log.error("")
            return MessageUtils.createMessage(update, "")
        }

        val dish = createOrUpdateDish(user, dishMenuNumber, dishName)

        return null
    }

    private fun createOrUpdateDish(user: UserEntity, dishMenuNumber: String, dishName: String): DishEntity {
        val existingDish = dishRepository.findDish(dishMenuNumber.toInt(), user.orderId!!)

        val dish = existingDish?.apply {
            getDishNameOrNullIfEmpty(dishName)?.let {
                Log.info("order ${user.orderId}: updating dish $menuNumber - $it")
                name = it
            }
        } ?: createDish(user, dishMenuNumber, dishName)

        dishRepository.persist(dish)

        return dish
    }

    private fun getDishNameOrNullIfEmpty(dishName: String): String? {
        return if (dishName == "") null else dishName
    }

    private fun createDish(user: UserEntity, dishMenuNumber: String, dishName: String): DishEntity {
        Log.info("order ${user.orderId}: adding dish $dishMenuNumber - $dishName")
        return DishEntity().apply {
            menuNumber = dishMenuNumber.toInt()
            name = getDishNameOrNullIfEmpty(dishName)
            order = user.order
        }
    }
}
