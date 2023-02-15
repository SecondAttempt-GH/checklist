package com.example.proverka.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

class ProductItem(
    var productId: Int,
    var productName: String?,
    var productQuantity: Int = 1,
    private var isChanged: Boolean = false,
    private var isRemoved: Boolean = false
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readBoolean(),
        parcel.readBoolean()
    )

    fun isRemoved(): Boolean {
        return isRemoved
    }

    fun isChanged(): Boolean {
        return isChanged
    }

    fun remove() {
        isRemoved = true
    }

    fun change() {
        isChanged = true
    }

    override fun toString(): String {
        return "$productName/$productQuantity"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(productId)
        parcel.writeString(productName)
        parcel.writeInt(productQuantity)
        parcel.writeBoolean(isChanged)
        parcel.writeBoolean(isRemoved)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductItem> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): ProductItem {
            return ProductItem(parcel)
        }

        override fun newArray(size: Int): Array<ProductItem?> {
            return arrayOfNulls(size)
        }
    }
}