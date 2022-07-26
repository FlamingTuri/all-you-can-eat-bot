package it.bot.model.command


class AddDishesCommand : BotCommand, KeyboardButton {

    override val command: String = "/addDishes"

    val addDishCommand = AddDishCommand()

    override val pattern: String = "(${addDishCommand.pattern}(\n${addDishCommand.pattern})*)"

    override val format: String = "{menuNumber} {quantity:1} {dishName:}"

    override val description: String = "same as /addDish but you can specify multiple dishes, one per line"

    override val commandType: CommandType = CommandType.Anywhere

    override fun isMultiline(): Boolean = true

    override fun getInlineKeyboardButtonText(): String = "add"

    override fun getCallbackData(): String = command
}
