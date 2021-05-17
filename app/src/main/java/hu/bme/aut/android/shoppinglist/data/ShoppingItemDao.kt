package hu.bme.aut.android.shoppinglist.data

import androidx.room.*

@Dao
interface ShoppingItemDao {
    @Query("select * from shoppingitem")
    suspend fun getAll(): List<ShoppingItem>

    @Query("select * from shoppingitem where id = :id")
    suspend fun getAt(id: Long): ShoppingItem

    @Insert
    suspend fun insert(shoppingItem: ShoppingItem): Long

    @Update
    suspend fun update(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteItem(shoppingItem: ShoppingItem)
}