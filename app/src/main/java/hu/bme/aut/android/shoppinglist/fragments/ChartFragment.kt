package hu.bme.aut.android.shoppinglist.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.data.ShoppingItem
import hu.bme.aut.android.shoppinglist.data.ShoppingItemCategory
import hu.bme.aut.android.shoppinglist.databinding.FragmentChartBinding
import hu.bme.aut.android.shoppinglist.databinding.FragmentItemDetailedBinding

class ChartFragment : DialogFragment() {

    companion object {
        const val TAG = "ChartFragment"
    }

    private lateinit var items: List<ShoppingItem>

    private lateinit var listener: ChartFragmentListener

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity: FragmentActivity = getActivity()!!
        listener = if (activity is ChartFragmentListener) {
            activity
        } else {
            throw RuntimeException("Activity must implement the ChartFragmentListener interface!")
        }
    }

    private fun initItems() {
        items = listener.getItems()

        binding.chart.description.isEnabled = false

        var sum = 0

        items.forEach {
            sum += it.estimatedPrice
        }

        binding.sumPrice.text = "$sum Ft"

        var food = 0F
        var elecs = 0F
        var books = 0F

        items.forEach {
            when (it.category) {
                ShoppingItemCategory.BOOK -> books += it.estimatedPrice
                ShoppingItemCategory.ELECTRONIC -> elecs += it.estimatedPrice
                ShoppingItemCategory.FOOD -> food += it.estimatedPrice
            }
        }

        if (sum > 0) {
            food /= sum
            elecs /= sum
            books /= sum
        }

        var entries: ArrayList<PieEntry> = ArrayList()

        if (food > 0) entries.add(PieEntry(food.toFloat(), "Food"))
        if (elecs > 0) entries.add(PieEntry(elecs.toFloat(), "Electronics"))
        if (books > 0) entries.add(PieEntry(books.toFloat(), "Books"))

        val dataSet = PieDataSet(entries, "| All items")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        dataSet.setDrawValues(false)

        val data = PieData(dataSet)
        binding.chart.data = data
        binding.chart.invalidate()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentChartBinding.inflate(LayoutInflater.from(context))

        initItems()

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.chart_title)
            .setView(binding.root)
            .setPositiveButton(R.string.ok, null)
            .create()
    }

    interface ChartFragmentListener {
        fun getItems(): List<ShoppingItem>
    }
}