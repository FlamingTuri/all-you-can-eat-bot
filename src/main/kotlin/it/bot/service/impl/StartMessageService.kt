package it.bot.service.impl

import it.bot.model.command.StartMessageCommand
import it.bot.service.interfaces.CommandParserService
import it.bot.util.MessageUtils
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@ApplicationScoped
class StartMessageService : CommandParserService() {

    override val botCommand = StartMessageCommand()

    @Transactional
    override fun parseUpdate(update: Update): SendMessage? {
        return super.parseUpdate(update)
    }

    override fun executeOperation(update: Update, matchResult: MatchResult): SendMessage {
        val messageText = """
            This bot help you manage orders for restaurants that use all you can eat formula.

            **Usage example:**
            # create a new order called 'my-order' for the current chat
            /createOrder my-order
            # join the order 'my-order'
            /joinOrder my-order
            # add menu number 6 one time (labelling it 'sashimi')
            /addDish 6 1 sashimi
            # add menu number 55 three times (labelling it 'nighiri')
            /addDish 22 3 nighiri
            # adds menu number 42 one time (it will not be labelled)
            /addDish 42
            # labels menu number 42 as 'uramaki'
            /nameDish 42 uramaki
            # close the order 'my-order' to prevent further modifications
            /closeOrder my-order

            To know the list of all the supported commands type /help
        """.trimIndent()

        return MessageUtils.createMessage(update, messageText, true)
    }
}
