package com.redsystemstudio.appcomprayventa

import android.content.Context
import com.google.gson.Gson
import com.redsystemstudio.appcomprayventa.Modelo.CarritoItem
import com.google.firebase.auth.FirebaseAuth

class CarritoManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("CARRITO_PREF", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun getUserId(): String {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.currentUser?.uid ?: ""
    }

    fun agregarAlCarrito(item: CarritoItem) {
        val carritoItems = obtenerCarrito().toMutableList()
        carritoItems.add(item)
        guardarCarrito(carritoItems)
    }

    fun eliminarDelCarrito(item: CarritoItem) {
        val carritoItems = obtenerCarrito().toMutableList()
        carritoItems.removeIf { it.id == item.id }
        guardarCarrito(carritoItems)
    }

    fun obtenerCarrito(): List<CarritoItem> {
        val carritoJson = sharedPreferences.getString(getUserId(), "[]")
        return gson.fromJson(carritoJson, Array<CarritoItem>::class.java).toList()
    }

    private fun guardarCarrito(carritoItems: List<CarritoItem>) {
        val editor = sharedPreferences.edit()
        val carritoJson = gson.toJson(carritoItems)
        editor.putString(getUserId(), carritoJson)
        editor.apply()
    }

    fun calcularSubtotal(): Double {
        val carritoItems = obtenerCarrito()
        return carritoItems.sumByDouble { it.precio * it.cantidad }
    }

    fun calcularTotalImpuestos(impuestoPorcentaje: Double): Double {
        val subtotal = calcularSubtotal()
        return subtotal * (impuestoPorcentaje / 100)
    }

    fun calcularTotal(impuestoPorcentaje: Double): Double {
        val subtotal = calcularSubtotal()
        val impuestos = calcularTotalImpuestos(impuestoPorcentaje)
        return subtotal + impuestos
    }
}
