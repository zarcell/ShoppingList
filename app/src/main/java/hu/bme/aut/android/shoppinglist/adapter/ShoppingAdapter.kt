package hu.bme.aut.android.shoppinglist.adapter

import android.app.Activity
import android.app.TabActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.data.ShoppingItem
import hu.bme.aut.android.shoppinglist.data.ShoppingItemCategory
import hu.bme.aut.android.shoppinglist.data.ShoppingItemDao
import hu.bme.aut.android.shoppinglist.databinding.ItemShoppingListBinding
import hu.bme.aut.android.shoppinglist.fragments.NewShoppingItemDialogFragment

class ShoppingAdapter(private val listener: ShoppingItemClickListener) :
    RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>() {
    private val items = mutableListOf<ShoppingItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ShoppingViewHolder(
        ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val shoppingItem = items[position]

        holder.binding.ivIcon.setImageResource(getImageResource(shoppingItem.category))
        holder.binding.cbIsBought.isChecked = shoppingItem.isBought
        holder.binding.tvName.text = shoppingItem.name
        holder.binding.tvPrice.text = "${shoppingItem.estimatedPrice} Ft"
        holder.binding.ibEdit.setOnClickListener {
            listener.onEditClicked(shoppingItem.id)
        }
        holder.binding.ibRemove.setOnClickListener {
            listener.onRemoveClicked(shoppingItem)
        }

        holder.binding.cbIsBought.setOnCheckedChangeListener { buttonView, isChecked ->
            shoppingItem.isBought = isChecked
            listener.onItemChanged(shoppingItem)
        }
        holder.binding.root.setOnClickListener {
            listener.onItemClicked(shoppingItem.id)
        }
    }

    @DrawableRes()
    private fun getImageResource(category: ShoppingItemCategory): Int {
        return when (category) {
            ShoppingItemCategory.FOOD -> R.drawable.groceries
            ShoppingItemCategory.ELECTRONIC -> R.drawable.lightning
            ShoppingItemCategory.BOOK -> R.drawable.open_book
        }
    }

    fun addItem(item: ShoppingItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun removeItem(item: ShoppingItem) {
        notifyItemRemoved(items.indexOf(item))
        items.remove(item)
    }

    fun updateItem(shoppingItem: ShoppingItem) {
        items.forEach {
            if (it.id == shoppingItem.id)
                items[items.indexOf(it)] = shoppingItem
        }
        notifyDataSetChanged()
    }

    fun update(shoppingItems: List<ShoppingItem>) {
        items.clear()
        items.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    interface ShoppingItemClickListener {
        fun onItemChanged(item: ShoppingItem)
        fun onRemoveClicked(shoppingItem: ShoppingItem)
        fun onEditClicked(id: Long)
        abstract fun onItemClicked(id: Long)
    }

    inner class ShoppingViewHolder(val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}
