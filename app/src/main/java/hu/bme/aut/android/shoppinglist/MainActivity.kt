package hu.bme.aut.android.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.shoppinglist.adapter.ShoppingAdapter
import hu.bme.aut.android.shoppinglist.data.ShoppingItem
import hu.bme.aut.android.shoppinglist.data.ShoppingListDatabase
import hu.bme.aut.android.shoppinglist.databinding.ActivityMainBinding
import hu.bme.aut.android.shoppinglist.fragments.ChartFragment
import hu.bme.aut.android.shoppinglist.fragments.ItemDetailedFragment
import hu.bme.aut.android.shoppinglist.fragments.NewShoppingItemDialogFragment
import kotlinx.coroutines.*

class MainActivity :
    AppCompatActivity(),
    ShoppingAdapter.ShoppingItemClickListener,
    NewShoppingItemDialogFragment.NewShoppingItemDialogListener,
    ItemDetailedFragment.ItemDetailedFragmentListener,
    ChartFragment.ChartFragmentListener,
    CoroutineScope by MainScope() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var database: ShoppingListDatabase
    private lateinit var adapter: ShoppingAdapter

    private fun initRecyclerView() {
        adapter = ShoppingAdapter(this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() = launch {
        val items = withContext(Dispatchers.IO) {
            database.shoppingItemDao().getAll()
        }
        adapter.update(items)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = ShoppingListDatabase.getDatabase(applicationContext)

        binding.fab.setOnClickListener {
            NewShoppingItemDialogFragment().show(supportFragmentManager, NewShoppingItemDialogFragment.TAG)
        }

        initRecyclerView()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.chart -> {
                ChartFragment().show(supportFragmentManager, ChartFragment.TAG)
                return true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemChanged(item: ShoppingItem) {
        updateItemInBackground(item)
    }

    override fun onRemoveClicked(shoppingItem: ShoppingItem) {
        removeItemInBackground(shoppingItem)
    }

    override fun onEditClicked(id: Long) {
        val newFragment = NewShoppingItemDialogFragment()
        newFragment.arguments = bundleOf("id" to id)
        newFragment.show(supportFragmentManager, NewShoppingItemDialogFragment.TAG)
    }

    override fun onItemClicked(id: Long) {
        val newFragment = ItemDetailedFragment()
        newFragment.arguments = bundleOf("id" to id)
        newFragment.show(supportFragmentManager, ItemDetailedFragment.TAG)
    }

    private fun updateItemInBackground(item: ShoppingItem) = launch {
        withContext(Dispatchers.IO) {
            database.shoppingItemDao().update(item)
        }
        adapter.updateItem(item)
    }

    override fun onShoppingItemCreated(item: ShoppingItem) {
        addItemInBackgound(item)
    }

    override fun getItemById(id: Long): ShoppingItem = runBlocking {
        withContext(Dispatchers.IO) {
            database.shoppingItemDao().getAt(id)
        }
    }

    override fun OnShoppingItemUpdated(shoppingItem: ShoppingItem) {
        updateItemInBackground(shoppingItem)
    }

    private fun addItemInBackgound(item: ShoppingItem) = launch {
        withContext(Dispatchers.IO) {
            item.id = database.shoppingItemDao().insert(item)
        }
        adapter.addItem(item)
    }

    private fun removeItemInBackground(item: ShoppingItem) = launch {
        withContext(Dispatchers.IO) {
            database.shoppingItemDao().deleteItem(item)
        }
        adapter.removeItem(item)
    }

    override fun getItems(): List<ShoppingItem> = runBlocking {
        withContext(Dispatchers.IO) {
            database.shoppingItemDao().getAll()
        }
    }
}