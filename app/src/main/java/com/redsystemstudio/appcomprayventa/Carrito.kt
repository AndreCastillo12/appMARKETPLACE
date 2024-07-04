package com.redsystemstudio.appcomprayventa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.redsystemstudio.appcomprayventa.Adaptadores.CarritoAdapter
import com.redsystemstudio.appcomprayventa.Modelo.CarritoItem
import com.redsystemstudio.appcomprayventa.databinding.ActivityCarritoBinding

class Carrito : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding
    private lateinit var carritoManager: CarritoManager
    private lateinit var adapter: CarritoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carritoManager = CarritoManager(this)

        binding.cartView.layoutManager = LinearLayoutManager(this)
        val carritoItems = carritoManager.obtenerCarrito().toMutableList()
        adapter = CarritoAdapter(carritoItems) { item ->
            carritoManager.eliminarDelCarrito(item)
            adapter.removeItem(item)
            actualizarTotales()
        }
        binding.cartView.adapter = adapter

        actualizarTotales()

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.checkoutBtn.setOnClickListener {
            val intent = Intent(this, PagoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun actualizarTotales() {
        val subtotal = carritoManager.calcularSubtotal()
        val impuestos = carritoManager.calcularTotalImpuestos(18.0) // Ejemplo de 18% de impuestos
        val total = carritoManager.calcularTotal(18.0)

        binding.totalFeeTxt.text = "S/. %.2f".format(subtotal)
        binding.taxTxt.text = "S/. %.2f".format(impuestos)
        binding.totalTxt.text = "S/. %.2f".format(total)
    }
}
