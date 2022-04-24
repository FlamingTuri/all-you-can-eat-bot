package it.bot.service.impl

import it.bot.repository.OrderRepository
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class LeaveOrderService(@Inject private val orderRepository: OrderRepository) : CommandParserService() {

    override val command = "/leaveOrder"

    override val commandPattern = "(\\s*)(\\w+)(\\s*)"

    override val commandFormat: String = "{orderName}"

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val (_, orderName, _) = matchResult.destructured

        val chatId = MessageUtils.getChatId(update)
        val order = orderRepository.findOpenOrderWithNameForChat(chatId, orderName)

        // TODO

        return null
    }
}
