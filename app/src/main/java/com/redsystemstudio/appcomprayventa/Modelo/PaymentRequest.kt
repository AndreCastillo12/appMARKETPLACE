data class PaymentRequest(
    val language: String,
    val command: String,
    val merchant: Merchant,
    val transaction: Transaction,
    val test: Boolean = true
)

data class Merchant(
    val apiLogin: String,
    val apiKey: String
)

data class Transaction(
    val order: Order,
    val payer: Payer,
    val creditCard: CreditCard?,
    val type: String = "AUTHORIZATION_AND_CAPTURE",
    val paymentMethod: String,
    val paymentCountry: String = "PE",
    val deviceSessionId: String,
    val ipAddress: String,
    val cookie: String,
    val userAgent: String
)

data class Order(
    val accountId: String,
    val referenceCode: String,
    val description: String,
    val language: String = "es",
    val signature: String,
    val additionalValues: AdditionalValues,
    val buyer: Buyer
)

data class AdditionalValues(
    val TX_VALUE: TX_VALUE
)

data class TX_VALUE(
    val value: Double,
    val currency: String = "PEN"
)

data class Buyer(
    val merchantBuyerId: String,
    val fullName: String,
    val emailAddress: String,
    val contactPhone: String,
    val dniNumber: String,
    val shippingAddress: ShippingAddress
)

data class ShippingAddress(
    val street1: String,
    val street2: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val phone: String
)

data class Payer(
    val merchantPayerId: String,
    val fullName: String,
    val emailAddress: String,
    val contactPhone: String,
    val dniNumber: String,
    val billingAddress: BillingAddress
)

data class BillingAddress(
    val street1: String,
    val street2: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val phone: String
)

data class CreditCard(
    val number: String,
    val securityCode: String,
    val expirationDate: String,
    val name: String
)

data class PaymentResponse(
    val code: String,
    val transactionResponse: TransactionResponse
)

data class TransactionResponse(
    val orderId: String,
    val transactionId: String,
    val state: String,
    val paymentNetworkResponseCode: String,
    val paymentNetworkResponseErrorMessage: String,
    val trazabilityCode: String,
    val authorizationCode: String,
    val pendingReason: String,
    val responseCode: String,
    val errorCode: String,
    val responseMessage: String,
    val transactionDate: String,
    val transactionTime: String,
    val operationDate: String,
    val referenceQuestionId: String,
    val merchantName: String,
    val merchantAddress: String
)
