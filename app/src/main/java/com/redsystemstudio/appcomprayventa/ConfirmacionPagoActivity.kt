package com.redsystemstudio.appcomprayventa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.redsystemstudio.appcomprayventa.databinding.ActivityConfirmacionPagoBinding

class ConfirmacionPagoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmacionPagoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmacionPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transactionId = intent.getStringExtra("TRANSACTION_ID")
        val total = intent.getStringExtra("TOTAL")

        binding.tvNumeroOrden.text = "Número de orden: $transactionId"
        binding.tvTotal.text = "Total: $total"

        binding.btnSeguirComprando.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}
