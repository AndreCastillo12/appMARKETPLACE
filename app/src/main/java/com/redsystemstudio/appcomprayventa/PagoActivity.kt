package com.redsystemstudio.appcomprayventa

import AdditionalValues
import BillingAddress
import Buyer
import Merchant
import Order
import PayUApiService
import Payer
import PaymentRequest
import PaymentResponse
import ShippingAddress
import TX_VALUE
import Transaction
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.internal.bind.TypeAdapters.UUID
import com.google.gson.internal.bind.TypeAdapters.UUID_FACTORY
import com.redsystemstudio.appcomprayventa.databinding.ActivityPagoBinding
import com.redsystemstudio.appcomprayventa.utils.SignatureUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class PagoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPagoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radioGroupMetodosPago.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbYape -> binding.layoutYape.visibility = View.VISIBLE
                else -> binding.layoutYape.visibility = View.GONE
            }
        }

        binding.btnPagar.setOnClickListener {
            if (binding.rbYape.isChecked) {
                realizarPagoConYape()
            } else {
                // Manejar otros métodos de pago
            }
        }
    }

    private fun realizarPagoConYape() {
        val apiService = RetrofitClient.instance.create(PayUApiService::class.java)

        val apiKey = "UcQ6Bilo89CxD2pp281v2v7rS4"
        val merchantId = "1019543"
        val referenceCode = "ORDER1"
        val amount = "100.00"
        val currency = "PEN"

        val signature = SignatureUtils.generarFirma(apiKey, merchantId, referenceCode, amount, currency)

        val merchant = Merchant(apiLogin = "nZo877xX0HZvwPI", apiKey = "UcQ6Bilo89CxD2pp281v2v7rS4")
        val order = Order(
            accountId = "1019543",
            referenceCode = referenceCode,
            description = "Compra de marketplace",
            signature = signature,
            additionalValues = AdditionalValues(TX_VALUE(value = 100.0)),
            buyer = Buyer(
                merchantBuyerId = "COMPRADOR_ID",
                fullName = "Nombre del comprador",
                emailAddress = "email@comprador.com",
                contactPhone = "123456789",
                dniNumber = "12345678",
                shippingAddress = ShippingAddress(
                    street1 = "Calle 123",
                    street2 = "Apt 456",
                    city = "Ciudad",
                    state = "Estado",
                    country = "PE",
                    postalCode = "0000",
                    phone = "123456789"
                )
            )
        )

        val transaction = Transaction(
            order = order,
            payer = Payer(
                merchantPayerId = "PAYER_ID",
                fullName = "Nombre del pagador",
                emailAddress = "email@pagador.com",
                contactPhone = "987654321",
                dniNumber = "87654321",
                billingAddress = BillingAddress(
                    street1 = "Calle 456",
                    street2 = "Apt 789",
                    city = "Ciudad",
                    state = "Estado",
                    country = "PE",
                    postalCode = "1111",
                    phone = "987654321"
                )
            ),
            creditCard = null, // No necesitas esta parte si usas Yape
            paymentMethod = "YAPE",
            deviceSessionId = "session12345",
            ipAddress = "192.168.0.1",
            cookie = "cookie123",
            userAgent = "Mozilla/5.0"
        )

        val paymentRequest = PaymentRequest(
            language = "es",
            command = "SUBMIT_TRANSACTION",
            merchant = merchant,
            transaction = transaction
        )

        apiService.createPayment(paymentRequest).enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                val rawJson = response.raw().body?.string() ?: ""
                Log.d("PagoActivity", "Raw JSON response: $rawJson")

                if (response.isSuccessful) {
                    val paymentResponse = response.body()
                    // Manejar la respuesta del pago
                    binding.resultadoPago.visibility = View.VISIBLE
                    binding.resultadoPago.text = "Pago exitoso: ${paymentResponse?.transactionResponse?.transactionId}"
                    mostrarConfirmacionPago(paymentResponse?.transactionResponse?.transactionId)
                } else {
                    // Manejar el error
                    Log.e("PagoActivity", "Error en el pago: $rawJson")
                    binding.resultadoPago.visibility = View.VISIBLE
                    binding.resultadoPago.text = "Error en el pago: $rawJson"
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                // Manejar la falla
                Log.e("PagoActivity", "Fallo en el pago: ${t.message}", t)
                binding.resultadoPago.visibility = View.VISIBLE
                binding.resultadoPago.text = "Fallo en el pago: ${t.message}"
            }
        })
    }

    private fun mostrarConfirmacionPago(transactionId: String?) {
        val intent = Intent(this, ConfirmacionPagoActivity::class.java).apply {
            putExtra("TRANSACTION_ID", transactionId)
            putExtra("TOTAL", "S/ 120") // Reemplazar con el total real
        }
        startActivity(intent)
    }
}
