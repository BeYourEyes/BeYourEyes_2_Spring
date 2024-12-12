package com.dna.beyoureyes.util

import android.util.Log
import com.dna.beyoureyes.model.ApiResponse
import com.dna.beyoureyes.model.Carbs
import com.dna.beyoureyes.model.Cholesterol
import com.dna.beyoureyes.model.Fat
import com.dna.beyoureyes.model.Food
import com.dna.beyoureyes.model.Natrium
import com.dna.beyoureyes.model.NutrientResult
import com.dna.beyoureyes.model.Nutrition
import com.dna.beyoureyes.model.Protein
import com.dna.beyoureyes.model.RetrofitClient
import com.dna.beyoureyes.model.SaturatedFat
import com.dna.beyoureyes.model.Sugar


// local.properties의 OPEN_API_KEY 설정 필요 !!!
class FoodOpenApiHelper {

    // String List(ex.OCR 결과)에서 [품목보고번호] 문자 추출
    // : "품목보고번호 102941237987923" 이런 문자열을 찾고 뒤 숫자만 추출하는 메소드로 추정.
    fun extractItemMnftrRptNo(lines: List<String>): String? {
        // 개선된 정규식
        // \\s*은 공백 존재 가능 의미
        // (\\d{12,})부분은 숫자만을 추출하되, 숫자가 12개 이상 연속으로 등장한다는 의미
        val itemMnftrRptNoPattern = "품목보고번호\\s*(\\d{12,})".toRegex()

        // 키워드를 포함하는 첫 번째 라인을 찾고, let 블록 내에서 숫자 추출 로직을 수행
        lines.firstOrNull { it.contains("품목보고번호") }?.let { line ->
            return itemMnftrRptNoPattern.find(line)?.groupValues?.get(1)
                ?: run {
                    null // 정규식과 일치하는 부분이 없으면 null 반환
                }
        }
        return null
    }

    // OPEN API 호출 결과를 moPercentList 형식(퍼센트값 문자열 리스트)으로 반환
    suspend fun processFoodData(itemMnftrRptNo: String): Food? {
        fetchFoodData("1", "100", "json", itemMnftrRptNo)?.let { (kcal, nutrientResults) ->
            val nutriList: List<Nutrition> = nutrientResults.map { result ->
                listOf(
                    Protein(result.prot),
                    Fat(result.fatce),
                    Carbs(result.chocdf),
                    Sugar(result.sugar),
                    Natrium(result.nat),
                    Cholesterol(result.chole),
                    SaturatedFat(result.fasat)
                )
            }.flatten()
            return Food(kcal, nutriList)
        }?:run {
            Log.d("OpenApiHelper", "No Food Data From Api Response")
            return null
        }
    }

    // OPEN API 호출 - 식품품목보고번호 이용
    suspend fun fetchFoodData(
        pageNo: String,
        numOfRows: String,
        type: String,
        itemMnftrRptNo: String
    ): Pair<Int, List<NutrientResult>>? {
        return try {
            Log.d("OpenApiHelper", "API Call Start")
            val response = RetrofitClient.apiService.getFood(
                pageNo = pageNo,
                numOfRows = numOfRows,
                type = type,
                itemMnftrRptNp = itemMnftrRptNo
            )
            Log.d("OpenApiHelper", "Got Response")
            val items = response.response.body.items
            Log.d("OpenApiHelper", "API Call Succeed")
            calculateNutrient(items) // foodSize 기준으로 mg 계산하여 반환
        } catch (e: Exception) {
            Log.d("OpenApiHelper", "API Call Error")
            Log.e("OpenApiHelper", "API 호출 실패: ${e.message}")
            null
        }
    }

    // OPEN API로 가져온 데이터 foodSize 기준으로 mg 계산
    // : 100g 기준으로 제공된 영양정보 -> foodSize(g?) 기준으로 mg 단위 함유량 계산
    private fun calculateNutrient(items: List<ApiResponse.Item>): Pair<Int, List<NutrientResult>> {
        val result = mutableListOf<NutrientResult>() // 리턴할 결과 데이터 객체
        var kcal = 0
        for (item in items) {
            // item.foodSize에서 숫자만 추출(아마... 단위 등 기타 문자열 제거 목적?)
            val rawFoodSize = item.foodSize
            val foodSize =
                rawFoodSize
                    .replace("[^\\d.]".toRegex(), "") // 정규식을 사용하여 숫자만 추출
                    .toDoubleOrNull() ?: 1.0 // Double 형변환, 실패 시 1.0 기본 값
            // Log.d("Debug_foodSize", "Raw foodSize: $rawFoodSize, Extracted foodSize: $foodSize")

            // 여기서 직접 Nutrient 형식 데이터로 만들어도 될 듯... 일단 왜 리스트 형식인지부터 알아보고
            // g 단위로 정보 제공됨
            val prot = ((item.prot.toDoubleOrNull() ?: 0.0) * 1000 / 100.0 * foodSize).toInt() // 단백질
            val fatce = ((item.fatce.toDoubleOrNull() ?: 0.0) * 1000 / 100.0 * foodSize).toInt() // 지방
            val chocdf = ((item.chocdf.toDoubleOrNull() ?: 0.0) * 1000 / 100.0 * foodSize).toInt() // 탄수
            val sugar = ((item.sugar.toDoubleOrNull() ?: 0.0) * 1000 / 100.0 * foodSize).toInt() // 당
            val fasat = ((item.fasat.toDoubleOrNull() ?: 0.0) * 1000 / 100.0 * foodSize).toInt() // 포화지방
            // mg 단위로 정보 제공됨
            val nat = ((item.nat.toDoubleOrNull() ?: 0.0) / 100.0 * foodSize).toInt() // 나트륨
            val chole = ((item.chole.toDoubleOrNull() ?: 0.0) / 100.0 * foodSize).toInt() // 콜레스테롤
            // Log.d("kcal_s", item.enerc)
            kcal = ((item.enerc.toDoubleOrNull() ?: 0.0)/ 100.0 * foodSize).toInt()

            val nutrientResult = NutrientResult(prot, fatce, chocdf, sugar, nat, chole, fasat)
            // Log.d("Debug_NutrientResult", nutrientResult.toString())
            result.add(nutrientResult)
            // Log.d("Nutri", result.toString())
        }
        return Pair(kcal, result)
    }
}