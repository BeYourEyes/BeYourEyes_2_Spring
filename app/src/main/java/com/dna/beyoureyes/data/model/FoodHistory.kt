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

    companion object {
        // FoodHistory 리스트에서 Nutrition 타입별 합산 값 반환 함수
        fun List<FoodHistory>.sumNutritions(): List<Nutrition> {
            return this.flatMap { it.nutritions } // 모든 nutritions 리스트를 하나로 펼침
                .groupBy { it::class } // Nutrition 타입별로 그룹화
                .mapNotNull { (type, group) ->
                    val totalValue = group.sumOf { it.milligram } // 그룹 내 milligram 값 합산
                    when (type) { // 해당 Nutrition 타입의 객체로 변환
                        Carbs::class -> Carbs(totalValue)
                        Sugar::class -> Sugar(totalValue)
                        Protein::class -> Protein(totalValue)
                        Fat::class -> Fat(totalValue)
                        SaturatedFat::class -> SaturatedFat(totalValue)
                        Cholesterol::class -> Cholesterol(totalValue)
                        Natrium::class -> Natrium(totalValue)
                        else -> null // 다른 타입이 존재할 경우 대비
                    }
                }
        }
    }
}
