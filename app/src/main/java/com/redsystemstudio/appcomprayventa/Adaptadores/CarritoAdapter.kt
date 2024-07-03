package com.redsystemstudio.appcomprayventa.Adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redsystemstudio.appcomprayventa.Modelo.CarritoItem
import com.redsystemstudio.appcomprayventa.databinding.ItemCarritoBinding

class CarritoAdapter(
    private val carritoItems: MutableList<CarritoItem>,
    private val onDeleteClickListener: (CarritoItem) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(val binding: ItemCarritoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = carritoItems[position]
        holder.binding.itemTitle.text = item.titulo
        holder.binding.itemPrice.text = "S/. %.2f".format(item.precio)
        holder.binding.itemQuantity.text = "Cantidad: ${item.cantidad}"

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClickListener(item)
        }
    }

    override fun getItemCount(): Int {
        return carritoItems.size
    }

    fun removeItem(item: CarritoItem) {
        val position = carritoItems.indexOf(item)
        if (position != -1) {
            carritoItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
