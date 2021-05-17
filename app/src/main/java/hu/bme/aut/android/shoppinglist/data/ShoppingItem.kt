package hu.bme.aut.android.shoppinglist.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.versionedparcelable.ParcelField
import com.google.android.material.shape.CornerFamily

enum class ShoppingItemCategory {
    FOOD, ELECTRONIC, BOOK;

    companion object {
        @TypeConverter
        @JvmStatic
        fun getByOrdinal(ordinal: Int): ShoppingItemCategory? {
            var ret: ShoppingItemCategory? = null
            for (cat in values()) {
                if (cat.ordinal == ordinal) {
                    ret = cat
                    break
                }
            }
            return ret
        }

        @TypeConverter
        @JvmStatic
        fun toInt(category: ShoppingItemCategory): Int {
            return category.ordinal
        }
    }
}

@Entity(tableName = "shoppingitem")
data class ShoppingItem (
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "category") var category: ShoppingItemCategory,
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "estimated_price") var estimatedPrice: Int = 0,
    @ColumnInfo(name = "is_bought") var isBought: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        TODO("category"),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeInt(estimatedPrice)
        parcel.writeByte(if (isBought) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShoppingItem> {
        override fun createFromParcel(parcel: Parcel): ShoppingItem {
            return ShoppingItem(parcel)
        }

        override fun newArray(size: Int): Array<ShoppingItem?> {
            return arrayOfNulls(size)
        }
    }
}