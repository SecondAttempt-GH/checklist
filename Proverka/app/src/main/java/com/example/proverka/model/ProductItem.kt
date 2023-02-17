package com.example.proverka.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

class ProductItem(
    var productId: Int,
    var productName: String?,
    var productQuantity: Int = 1,
    private var isChanged: Int = 0,
    private var isRemoved: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    fun isRemoved(): Boolean {
        return isRemoved == 1
    }

    fun isChanged(): Boolean {
        return isChanged == 1
    }

    fun remove() {
        isRemoved = 1
    }

    fun change() {
        isChanged = 1
    }

    override fun toString(): String {
        return "$productName/$productQuantity"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(productId)
        parcel.writeString(productName)
        parcel.writeInt(productQuantity)
        parcel.writeInt(isChanged)
        parcel.writeInt(isRemoved)
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