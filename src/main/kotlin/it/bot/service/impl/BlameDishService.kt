package it.bot.service.impl

import io.quarkus.runtime.Startup
import it.bot.client.rest.TelegramRestClient
import it.bot.model.dto.TelegramUserDto
import it.bot.service.interfaces.CommandParserService
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
    @ConfigProperty(name = "bot.token") private val botToken: String
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

        return MessageUtils.createMessage(update, "asd")
    }

    private fun destructure(matchResult: MatchResult): Pair<Int, String?> {
        val (_, dishMenuNumber, _, orderName) = matchResult.destructured
        return Pair(dishMenuNumber.toInt(), if (orderName == "") null else orderName)
    }

    private fun getChatMember(update: Update): TelegramUserDto? {
        // TODO: understand why deserialization does not work and it should be done manually
        return telegramRestClient.getChatMember(
            botToken, MessageUtils.getChatId(update), MessageUtils.getTelegramUserId(update)
        ).result["user"]?.let {
            TelegramUserDto(
                it["id"] as Long,
                it["is_bot"] as Boolean,
                it["first_name"] as String,
                it["last_name"] as String,
                it["username"] as String,
                it["language_code"] as String
            )
        }
    }
}