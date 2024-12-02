package com.dna.beyoureyes.model

import android.os.Parcel
import android.os.Parcelable

// Parcelable 정의를 위한 래퍼 클래스
data class NutritionWrapper(val nutrition: Nutrition) : Parcelable {

    constructor(parcel: Parcel) : this(
        when (parcel.readString()) {
            Carbs.NAME -> Carbs(parcel.readInt())
            Cholesterol.NAME -> Cholesterol(parcel.readInt())
            Fat.NAME -> Fat(parcel.readInt())
            Natrium.NAME -> Natrium(parcel.readInt())
            Protein.NAME -> Protein(parcel.readInt())
            SaturatedFat.NAME -> SaturatedFat(parcel.readInt())
            Sugar.NAME -> Sugar(parcel.readInt())
            else -> throw IllegalArgumentException("Invalid nutrition type")
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        when (nutrition) {
            is Carbs -> {
                parcel.writeString(Carbs.NAME)
                parcel.writeInt(nutrition.milligram)
            }
            is Cholesterol -> {
                parcel.writeString(Cholesterol.NAME)
                parcel.writeInt(nutrition.milligram)
            }
            is Fat -> {
                parcel.writeString(Fat.NAME)
                parcel.writeInt(nutrition.milligram)
            }
            is Natrium -> {
                parcel.writeString(Natrium.NAME)
                parcel.writeInt(nutrition.milligram)
            }
            is Protein -> {
                parcel.writeString(Protein.NAME)
                parcel.writeInt(nutrition.milligram)
            }
            is SaturatedFat -> {
                parcel.writeString(SaturatedFat.NAME)
                parcel.writeInt(nutrition.milligram)
            }
            is Sugar -> {
                parcel.writeString(Sugar.NAME)
                parcel.writeInt(nutrition.milligram)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NutritionWrapper> {
        override fun createFromParcel(parcel: Parcel): NutritionWrapper {
            return NutritionWrapper(parcel)
        }

        override fun newArray(size: Int): Array<NutritionWrapper?> {
            return arrayOfNulls(size)
        }
    }
}