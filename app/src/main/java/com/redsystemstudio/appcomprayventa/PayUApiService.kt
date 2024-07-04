


import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PayUApiService {
    @Headers("Content-Type: application/json")
    @POST("payments-api/4.0/service.cgi")
    fun createPayment(@Body paymentRequest: PaymentRequest): retrofit2.Call<PaymentResponse>
}
