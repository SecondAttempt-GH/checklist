package com.example.proverka.model

import android.os.Parcel
import android.os.Parcelable

class FoodItem(
    var id: Long,
    var name: String?,
    var num: Long): Parcelable
 {

     constructor(parcel: Parcel) : this(
         parcel.readLong(),
         parcel.readString(),
         parcel.readLong()
     ) {
     }

     override fun toString(): String {
        return name+"/"+num
    }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
         parcel.writeLong(id)
         parcel.writeString(name)
         parcel.writeLong(num)
     }

     override fun describeContents(): Int {
         return 0
     }

     companion object CREATOR : Parcelable.Creator<FoodItem> {
         override fun createFromParcel(parcel: Parcel): FoodItem {
             return FoodItem(parcel)
         }

         override fun newArray(size: Int): Array<FoodItem?> {
             return arrayOfNulls(size)
         }
     }
 }