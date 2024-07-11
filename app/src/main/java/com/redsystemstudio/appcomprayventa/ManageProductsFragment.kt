package com.redsystemstudio.appcomprayventa

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.redsystemstudio.appcomprayventa.Modelo.ModeloAnuncio
import com.redsystemstudio.appcomprayventa.databinding.ActivityManegeProductsBinding

class ManegeProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManegeProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<ModeloAnuncio>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManegeProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference.child("Anuncios")

        // Configurar el RecyclerView
        productAdapter = ProductAdapter(productList, onEditClick = { product ->
            // Lógica para editar el producto
        }, onDeleteClick = { product ->
            // Lógica para eliminar el producto
            eliminarProducto(product)
        })

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = productAdapter

        // Cargar productos desde la base de datos
        loadProducts()

        // Configurar el buscador
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterProducts(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadProducts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.child("Imagenes").getValue(ModeloAnuncio::class.java)
                    product?.let {
                        it.id = productSnapshot.key ?: ""
                        productList.add(it)
                    }
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

    private fun filterProducts(query: String) {
        val filteredList = productList.filter { product ->
            product.titulo.contains(query, ignoreCase = true) || product.descripcion.contains(query, ignoreCase = true)
        }
        productAdapter.updateList(filteredList)
    }

    private fun eliminarProducto(product: ModeloAnuncio) {
        database.child(product.id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Eliminar producto de la lista local
                productList.remove(product)
                productAdapter.notifyDataSetChanged()
            } else {
                // Manejar error
            }
        }
    }
}
