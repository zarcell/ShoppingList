package hu.bme.aut.android.shoppinglist.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.data.ShoppingItem
import hu.bme.aut.android.shoppinglist.data.ShoppingItemCategory
import hu.bme.aut.android.shoppinglist.databinding.DialogNewShoppingItemBinding
import hu.bme.aut.android.shoppinglist.databinding.FragmentItemDetailedBinding

class ItemDetailedFragment : DialogFragment() {

    companion object {
        const val TAG = "ItemDetailedFragment"
    }

    private lateinit var item: ShoppingItem

    private lateinit var listener: ItemDetailedFragmentListener

    private var _binding: FragmentItemDetailedBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity: FragmentActivity = getActivity()!!
        listener = if (activity is ItemDetailedFragmentListener) {
            activity
        } else {
            throw RuntimeException("Activity must implement the ItemDetailedFragmentListener interface!")
        }
    }

    private fun initItem() {
        item = listener.getItemById(arguments?.getLong("id")!!)
        binding.tvName.text = item.name
        binding.tvDescription.text = item.description
        binding.tvEstimatedPrice.text = item.estimatedPrice.toString() + " Ft"
        binding.ivIcon.setImageResource(getImageResource(item.category))
    }

    @DrawableRes()
    private fun getImageResource(category: ShoppingItemCategory): Int {
        return when (category) {
            ShoppingItemCategory.FOOD -> R.drawable.groceries
            ShoppingItemCategory.ELECTRONIC -> R.drawable.lightning
            ShoppingItemCategory.BOOK -> R.drawable.open_book
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentItemDetailedBinding.inflate(LayoutInflater.from(context))

        initItem()

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.item_details_name)
            .setView(binding.root)
            .setPositiveButton(R.string.ok, null)
            .create()
    }

    interface ItemDetailedFragmentListener {
        fun getItemById(id: Long): ShoppingItem
    }
}