package it.bot.service.impl

import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import it.bot.client.rest.TelegramRestClient
import it.bot.model.dto.TelegramUserDto
import it.bot.model.entity.UserDishEntity
import it.bot.repository.UserDishRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.FormatUtils
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Startup
@ApplicationScoped
class BlameDishService(
    @ConfigProperty(name = "bot.token") private val botToken: String,
    @Inject private val userDishRepository: UserDishRepository
) : CommandParserService() {

    @Inject
    @field: RestClient // https://github.com/quarkusio/quarkus/issues/5413
    private lateinit var telegramRestClient: TelegramRestClient

    override val command: String = "/blame"

    override val commandPattern: String = "(\\s*)(\\d+)((\\s+)(\\w+))?"

    override val commandFormat: String = "{menuNumber} {orderName:}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val (dishMenuNumber, orderName) = destructure(matchResult)

        val userDishes = userDishRepository.findUserDishes(dishMenuNumber, orderName, MessageUtils.getChatId(update))

        val message = if (userDishes.isEmpty()) {
            getUserDishesNotFoundError(dishMenuNumber, orderName, update)
        } else {
            getUserDishesFoundMessage(userDishes, update)
        }

        return MessageUtils.createMessage(update, message)
    }

    private fun destructure(matchResult: MatchResult): Pair<Int, String?> {
        val (_, dishMenuNumber, _, _, orderName) = matchResult.destructured
        return Pair(dishMenuNumber.toInt(), if (orderName == "") null else orderName)
    }

    private fun getUserDishesNotFoundError(dishMenuNumber: Int, orderName: String?, update: Update): String {
        Log.error(
            "dish number $dishMenuNumber not found " +
                    "for chat ${MessageUtils.getChatId(update)} and order name $orderName"
        )
        return "Error: dish $dishMenuNumber not found for orders, " +
                "make sure you used /blame command in the correct chat"
    }

    private fun getUserDishesFoundMessage(userDishes: List<UserDishEntity>, update: Update): String {
        val dish = userDishes.first().dish

        Log.info(
            "Dish ${dish?.menuNumber} was ordered by users " +
                    userDishes.joinToString(", ") { it.dishId.toString() }
        )

        return "Dish ${dish?.menuNumber} ${FormatUtils.wrapIfNotNull(dish?.name)} was ordered by:" +
                "\n\n${formatUsersWhoOrderedDish(update, userDishes)}"
    }

    private fun formatUsersWhoOrderedDish(update: Update, userDishes: List<UserDishEntity>): String {
        return userDishes.joinToString("\n") {
            "- ${FormatUtils.tagUsername(getUserName(it))} x ${it.quantity}"
        }
    }

    private fun getUserName(userDish: UserDishEntity): String? {
        val chatId = userDish.dish?.order?.chatId
        val telegramUserId = userDish.user?.telegramUserId
        return if (chatId == null || telegramUserId == null) {
            Log.error(
                "Warning: chatId $chatId or telegramUserId $telegramUserId are null, userDish ${userDish.userDishId}"
            )
            null
        } else {
            Log.info("Getting username for user $telegramUserId, chat $chatId")
            getChatMember(chatId, telegramUserId)?.username
        }
    }

    private fun getChatMember(chatId: Long, telegramUserId: Long): TelegramUserDto? {
        try {
            // TODO: understand why deserialization does not work and it should be done manually
            val response = telegramRestClient.getChatMember(
                botToken, chatId, telegramUserId
            )

            return response.result["user"]?.let { it ->
                (it as Map<*, *>)
            }?.let {
                TelegramUserDto(
                    "${it["id"]}".toLong(),
                    it["is_bot"] as Boolean,
                    it["first_name"] as String,
                    it["last_name"] as String,
                    it["username"] as String,
                    it["language_code"] as String
                )
            }
        } catch (exception: Exception) {
            Log.error(
                "could not retrieve information for user $telegramUserId in chat $chatId", exception
            )
            return null
        }
    }
}