package it.bot.service.impl

import it.bot.service.interfaces.CommandParserService
import javax.enterprise.context.ApplicationScoped
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class AddDishService : CommandParserService() {

    override val command: String = "/addDish"

    override val commandPattern: String = "(\\s*)(\\d+)((\\s+)(\\d+))?((\\s+)(\\w+))?"

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage? {
        val (_, dishMenuNumber, _, _, dishQuantity, _, _, dishName) = matchResult.destructured

        return null
    }
}
