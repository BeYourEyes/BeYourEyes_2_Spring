package com.dna.beyoureyes.data.model

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.threeten.bp.LocalDateTime

// 0. 기본 생성자는 사진 uri 안 받음.
data class FoodHistory(
    val imgRefPath: String,
    val timestamp:LocalDateTime,
    val kcal: Int,
    val nutritions: List<Nutrition>,
    )
{
    val imgUrl: String? get() = _imgUrl // 읽기 전용
    private var _imgUrl :String? = null // 쓰기는 내부에서만
    init {
        CoroutineScope(Dispatchers.IO).launch {
            _imgUrl = try {
                val storageRef = FirebaseStorage.getInstance().reference.child(imgRefPath)
                storageRef.downloadUrl.await().toString() // 비동기 작업을 동기적으로 기다림
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}
