package com.dna.beyoureyes.model

import android.net.Uri
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

class FoodHistory(
    val timestamp:Timestamp? = null,
    val kcal: Int? = null,
    val nutritions: List<Nutrition>,
    var imgUri: Uri? = null) {

    constructor(document: DocumentSnapshot) : this(
        timestamp = document.getTimestamp("date"),
        kcal = document.getLong("calories")?.toInt() ?:0,
        nutritions = listOf(
                Natrium(), Carbs(), Sugar(), Protein(), Fat(), SaturatedFat(), Cholesterol()
            ).mapNotNull { type -> type.fromFirestore(document) })
    {
        val imgPath = document.getString("imgPath")
        // 이미지 Uri 로드는 파이어스토어와의 통신이 필요하므로 CouroutineScoope에서 정의하여 값 동기화
        CoroutineScope(Dispatchers.Main).launch {
            imgUri = try {
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
}
