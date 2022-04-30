package it.bot.model.messages

object OrderMessages {

    const val orderWithTheSameNameError = "Error: an order with the same name already exists for current chat"

    fun orderCreationSuccessful(orderName: String) = "Successfully created order '$orderName'"

    fun orderNotFoundError(orderName: String) = "Error: order '$orderName' not found for the current chat"
}
