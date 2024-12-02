package com.dna.beyoureyes.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// 0. 기본 생성자는 사진 uri 안 받음.
class FoodHistory(
    val timestamp:Timestamp? = null,
    val kcal: Int? = null,
    val nutritions: List<Nutrition>) : Parcelable { // 뷰간 데이터 주고받기 위한 Parcelable로 정의

    val imgUri: Uri? get() = _imgUri // 읽기 전용
    private var _imgUri :Uri? = null // 쓰기는 내부에서만

    // 1. uri 값 전달을 포함하는 생성자
    constructor(timestamp:Timestamp?, kcal: Int?, nutritions: List<Nutrition>, imgUri: Uri?)
            : this(timestamp, kcal, nutritions) { _imgUri = imgUri }

    // 2. FireStore 문서 기반 생성자
    constructor(document: DocumentSnapshot) : this(
        timestamp = document.getTimestamp("date"),
        kcal = document.getLong("calories")?.toInt() ?:0,
        nutritions =
            listOf(Natrium(), Carbs(), Sugar(), Protein(), Fat(), SaturatedFat(), Cholesterol())
            .mapNotNull { type -> type.fromFirestore(document) })
    {
        // 이미지 Uri 로드는 파이어스토어와의 통신이 필요하므로 CouroutineScoope에서 정의하여 값 동기화
        val imgPath = document.getString("imgPath")
        CoroutineScope(Dispatchers.Main).launch {
            _imgUri = try {
                imgPath?.let {
                    val storageRef = Firebase.storage.reference.child(it)
                    storageRef.downloadUrl.await() // 비동기 작업을 동기적으로 기다림
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // 3. Parcel 생성자
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(NutritionWrapper.CREATOR)!!.map { it.nutrition }, // Use NutritionWrapper.CREATOR
        parcel.readParcelable(Uri::class.java.classLoader)
    )

    // 기타 Parcelable 관련 정의
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(timestamp, flags)
        parcel.writeValue(kcal)
        parcel.writeTypedList(nutritions.map { NutritionWrapper(it) }) // Write the list of NutritionWrapper
        parcel.writeParcelable(imgUri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodHistory> {
        override fun createFromParcel(parcel: Parcel): FoodHistory {
            return FoodHistory(parcel)
        }

        override fun newArray(size: Int): Array<FoodHistory?> {
            return arrayOfNulls(size)
        }
    }
}
