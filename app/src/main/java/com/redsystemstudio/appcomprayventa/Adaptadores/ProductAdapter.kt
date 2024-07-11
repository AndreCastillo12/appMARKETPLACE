package com.redsystemstudio.appcomprayventa

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.redsystemstudio.appcomprayventa.Modelo.ModeloAnuncio

class ProductAdapter(
    private var productList: List<ModeloAnuncio>,
    private val onEditClick: (ModeloAnuncio) -> Unit,
    private val onDeleteClick: (ModeloAnuncio) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        Log.d("ProductAdapter", "Binding product: $product")
        holder.nameTextView.text = product.titulo
        holder.descriptionTextView.text = product.descripcion
        holder.editButton.setOnClickListener { onEditClick(product) }
        holder.deleteButton.setOnClickListener { onDeleteClick(product) }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateList(newList: List<ModeloAnuncio>) {
        productList = newList
        notifyDataSetChanged()
    }
}
