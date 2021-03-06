package it.bot.model.messages

object OrderMessages {

    const val orderWithTheSameNameError = "Error: an order with the same name already exists for current chat"

    fun orderCreationSuccessful(orderName: String) = "Successfully created order '$orderName'"

    fun orderNotFoundError(orderName: String) = "Error: order '$orderName' not found for the current chat"

    fun operationNotAllowedForClosedOrderError(orderName: String) =
        "Error: you cannot perform this operation when order '$orderName' is closed"

    fun orderCanBeReopenedError(orderName: String) =
        "Error: you cannot join another order since the order '$orderName' can still be reopened"
}
