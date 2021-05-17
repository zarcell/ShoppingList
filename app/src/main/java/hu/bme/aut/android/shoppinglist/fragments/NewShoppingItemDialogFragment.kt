package hu.bme.aut.android.shoppinglist.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.data.ShoppingItem
import hu.bme.aut.android.shoppinglist.data.ShoppingItemCategory
import hu.bme.aut.android.shoppinglist.databinding.DialogNewShoppingItemBinding

class NewShoppingItemDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "NewShoppingItemDialogFragment"
    }

    private lateinit var item: ShoppingItem

    private lateinit var listener: NewShoppingItemDialogListener

    private var _binding: DialogNewShoppingItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity: FragmentActivity = getActivity()!!
        listener = if (activity is NewShoppingItemDialogListener) {
            activity
        } else {
            throw RuntimeException("Activity must implement the NewShoppingItemDialogListener interface!")
        }
    }

    private fun initItem() {
        item = listener.getItemById(arguments?.getLong("id")!!)
        binding.etName.setText(item.name)
        binding.cbAlreadyPurchased.isChecked = item.isBought
        binding.etDescription.setText(item.description)
        binding.etEstimatedPrice.setText(item.estimatedPrice.toString())
        binding.spCategory.setSelection(item.category.ordinal)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogNewShoppingItemBinding.inflate(LayoutInflater.from(context))
        binding.spCategory.adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.category_items)
        )

        if (arguments?.getLong("id") != null)
            initItem()

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.new_shopping_item)
            .setView(binding.root)
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    if (arguments?.getLong("id") == null) {
                        listener.onShoppingItemCreated(getShoppingItem())
                    }
                    else{
                        val temp = getShoppingItem()
                        temp.id = arguments?.getLong("id")!!
                        listener.OnShoppingItemUpdated(temp)
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid(): Boolean {
        return binding.etName.text.isNotEmpty()
    }

    private fun getShoppingItem(): ShoppingItem {
        return ShoppingItem(
            name = binding.etName.text.toString(),
            description = binding.etDescription.text.toString(),
            estimatedPrice = binding.etEstimatedPrice.text.toString().toIntOrNull() ?: 0,
            category = ShoppingItemCategory.getByOrdinal(binding.spCategory.selectedItemPosition)
                ?: ShoppingItemCategory.BOOK,
            isBought = binding.cbAlreadyPurchased.isChecked
        )
    }


    interface NewShoppingItemDialogListener {
        fun onShoppingItemCreated(item: ShoppingItem)
        fun getItemById(id: Long): ShoppingItem
        abstract fun OnShoppingItemUpdated(shoppingItem: ShoppingItem)
    }
}
