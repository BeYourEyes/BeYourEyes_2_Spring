package com.dna.beyoureyes.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dna.beyoureyes.data.model.Allergen
import com.dna.beyoureyes.data.model.Carbs
import com.dna.beyoureyes.data.model.Cholesterol
import com.dna.beyoureyes.data.model.Fat
import com.dna.beyoureyes.data.model.Food
import com.dna.beyoureyes.data.model.Natrium
import com.dna.beyoureyes.data.model.Nutrition
import com.dna.beyoureyes.data.model.Protein
import com.dna.beyoureyes.data.model.SaturatedFat
import com.dna.beyoureyes.data.model.Sugar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.text.split


class FoodTextRecognizer(private val context: Context) {

    companion object {
        // 키워드들 HashSet으로 변경. 검색 효율 위해.
        val NUTRI_PREFIX_KEYWORDS = hashSetOf(
            "나트", "탄수", "지방", "당류", "트랜스", "포화", "콜레", "단백"
        )
        const val NATRIUM_DEFAULT_DV_MILLI = 2000
        const val CARBS_DEFAULT_DV_MILLI = 324 * 1000
        const val SUGAR_DEFAULT_DV_MILLI = 100 * 1000
        const val FAT_DEFAULT_DV_MILLI = 54 * 1000
        const val SATFAT_DEFAULT_DV_MILLI = 15 * 1000
        const val CHOLESTEROL_DEFAULT_DV_MILLI = 300
        const val PROTEIN_DEFAULT_DV_MILLI = 55 * 1000
    }

    // 분석 진행도를 다루는 StateFlow -> 로딩 화면에서 진행도 파악하기 위해
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    private val textRecognizer = TextRecognition.getClient(
        KoreanTextRecognizerOptions.Builder().build() // 한글 텍스트 인식 인스턴스 생성
    )
    private val apiHelper = FoodOpenApiHelper() // open API 통신 헬퍼 객체

    suspend fun detectTextFromUri(uri: Uri): Food? {
        return withContext(Dispatchers.Default) { // IO 스레드에서 실행(네트워크 작업, 파일 I/O 최적화)
            try {
                val image = InputImage.fromFilePath(context, uri)
                _progress.value = 5
                val result = withContext(Dispatchers.IO) {
                    Log.d("OCR", "Start Waiting For ML kit Response")
                    textRecognizer.process(image).await() // await()으로 결과 대기
                }
                _progress.value = 30
                Log.d("OCR", "Got ML kit Response")
                val lines = result.text.split('\n')
                val allergyData = extractAllergyData(lines) // 알레르기 정보 추출
                Log.d("Allergy", "${allergyData}")
                _progress.value = 45
                Log.d("OCR", "Extracted Allergy Data")
                val itemMnftrRptNo = apiHelper.extractItemMnftrRptNo(lines) // 품목보고 번호 추출
                _progress.value = 50
                if (itemMnftrRptNo != null) { // 품목보고번호가 있는 경우: OPEN API 호출
                    Log.d("OCR", "Manufact Number Exists.")
                    val apiData = withContext(Dispatchers.IO) {
                        apiHelper.processFoodData(itemMnftrRptNo)
                    }
                    // API 통신 성공시 알레르기 정보만 추가하고 실패시 다시 OCR 기반 데이터 추출
                    apiData?.let { apiData ->
                        Log.d("OCR", "Got Food Data From Open Api Response")
                        _progress.value = 100
                        apiData.setAllergyData(allergyData)
                        validateFoodData(apiData) // api 데이터 기반 리턴
                    } ?: extractNutriDataFromOcrResult(result).let { ocrData ->
                        Log.d("OCR", "Got Food Data From OCR Result")
                        _progress.value = 100
                        ocrData.setAllergyData(allergyData)
                        validateFoodData(ocrData) // ocr 기반 리턴
                    }
                } else { // 품목보고번호가 없는 경우: OCR 데이터 처리
                    Log.d("OCR", "No Manufact Number.")
                    extractNutriDataFromOcrResult(result).let { ocrData ->
                        Log.d("OCR", "Got Food Data From OCR Result")
                        _progress.value = 100
                        ocrData.setAllergyData(allergyData)
                        validateFoodData(ocrData) // ocr 기반 리턴
                    }
                }
            } catch (e: Exception) {
                Log.d("OCR", e.toString())
                null // 오류 발생 시 null 리턴
            }
        }
    }

    // 정보가 하나라도 있는지 최종 검증. 없으면 null 있으면 그대로 Food 데이터 반환
    fun validateFoodData(food: Food): Food? {
        return if ((food.kcal != null && food.nutritions != null) || food.allergy != null) food
        else null
    }

    // OCR API 호출 함수
    private fun extractNutriDataFromOcrResult(result: Text): Food {
        // val koreanCharacters = mutableSetOf<String>() // 영양소명 추출할 HashSet
        val gList = mutableListOf<Int>() // "g/mg" 키워드 기반 숫자 List
        val percentList = mutableListOf<Int>() // "%" 키워드 기반 숫자 List
        val kcalList = mutableListOf<Int>() // "kcal" 키워드 기반 숫자 List

        result.textBlocks.flatMap { it.lines }
            .forEach { line ->
                val lineText = line.text

                // OCR에서 데이터를 추출 mg, %, kca, g당
                extractNutrientData(lineText, gList, percentList, kcalList)

                // 영양소명 추출을 위한 키워드 처리 -> 실질적으로 사용 x ?
                /*
                line.elements.forEach { element ->
                    val elementText = element.text
                    if (NUTRI_PREFIX_KEYWORDS.any { keyword -> elementText.contains(keyword) }) {
                        koreanCharacters.add(
                            elementText.replace(Regex("[^가-힣]"), "") // 한글만
                        )
                    }
                }
                 */
            }

        // kcalList에서 총 kcal값만 추출(또는 계산)
        // 0, 2000 값은 잘못 인식 됐을 확률 높음 -> 제외
        var kcal = when (kcalList.filterNot { it in listOf(0, 2000) }.size) {
            1 -> kcalList[0] // 추출된 값 1개일 때, 추가 조치 필요 X. 바로 숫자만 추출.
            2 -> {
                /*
                이 부분 로직이 뭔지 재확인 필요. 원래 작성 되어 있던 계산 단계는 아래와 같음
                // val result = firstKcal.toDouble() / secondKcal.toDouble() // 1g당 칼로리 계산
                // val gListValue = (gList.firstOrNull() ?: 1.0).toDouble() // gList의 첫번째 인덱스 값 가져옴(총 용량.. 으로 추정?)
                // kcal = (result * gListValue).toInt() // 총 kcal 값 계산
                 */
                val (firstKcal, secondKcal) = kcalList
                if (secondKcal != 0) {
                    ((firstKcal.toDouble() / secondKcal) * (gList.firstOrNull()
                        ?: 1.0).toDouble()).toInt()
                } else null
            }

            else -> null // kcalList 크기가 0 또는 3 이상인 경우 오류
        }

        // % 정보를 -> g 정보로 변형하여 영양소 함유량 가져오기
        val nutritionList = getNutritionListFromPercentList(percentList)

        return Food(kcal, nutritionList)
    }

    private fun extractNutrientData(
        lineText: String,
        gList: MutableList<Int>,
        percentList: MutableList<Int>,
        kcalList: MutableList<Int>
    ) {
        // mg 키워드로 모든 숫자값 추출 (g/mg/9)
        val gRegex = """(\d+(?:\.\d+)?)\s?(?:g|mg|9)(?!\s*당)""".toRegex()
        gList.addAll(
            gRegex.findAll(lineText)
                .map { it.groupValues[1].toIntOrNull() }
                .filterNotNull()
                .toList()
        )

        // % 키워드로 모든 숫자값 추출
        val percentRegex = """(\d+(?:\.\d+)?)\s*%""".toRegex()
        percentList.addAll(
            percentRegex.findAll(lineText)
                .map { it.groupValues[1].toIntOrNull() }
                .filterNotNull()
                .map { it.coerceIn(0, 1000) }
                .toList()
        )

        // kcal 키워드로 모든 숫자값 추출
        val kcalRegex = """(\d+(?:\.\d+)?)\s*kcal""".toRegex()
        kcalList.addAll(
            kcalRegex.findAll(lineText)
                .map { it.groupValues[1].replace("[^\\d.]".toRegex(), "").toIntOrNull() }
                .filterNotNull()
                .toList()
        )
    }

    // % 를 이용하여 mg으로 계산
    private fun getNutritionListFromPercentList(percentList: List<Int>): List<Nutrition>? {
        if (percentList.size < 7) { // 조건에 안 맞으면 빈 리스트를 반환
            return null
        }
        val nutritionList = percentList.mapIndexed { index, percent ->
            when (index) {
                0 -> Natrium((percent.toDouble() * 0.01 * NATRIUM_DEFAULT_DV_MILLI).toInt())     // 나트륨
                1 -> Carbs((percent.toDouble() * 0.01 * CARBS_DEFAULT_DV_MILLI).toInt())       // 탄수화물
                2 -> Sugar((percent.toDouble() * 0.01 * SUGAR_DEFAULT_DV_MILLI).toInt())       // 당류
                3 -> Fat((percent.toDouble() * 0.01 * FAT_DEFAULT_DV_MILLI).toInt())         // 지방
                4 -> SaturatedFat((percent.toDouble() * 0.01 * SATFAT_DEFAULT_DV_MILLI).toInt())      // 포화지방
                5 -> Cholesterol((percent.toDouble() * 0.01 * CHOLESTEROL_DEFAULT_DV_MILLI).toInt()) // 콜레스테롤
                6 -> Protein((percent.toDouble() * 0.01 * PROTEIN_DEFAULT_DV_MILLI).toInt())     // 단백질
                else -> Carbs(0) // 기본값
            }
        }.take(7)
        return nutritionList
    }

    // 알레르기 추출 함수
    private fun extractAllergyData(lines: List<String>): Set<Allergen>? {
        val targetEndIndex = lines.indexOfFirst { it.contains("함유") } // "함유" 위치 찾기

        if (targetEndIndex == -1) return null
        val targetLines =
            lines.subList(maxOf(0, targetEndIndex - 1), lines.size)
        // "함유" 키워드가 존재하는 라인과 바로 직전 라인을 targetLines로 추출

        // 추출값 저장할 Mutable Set
        val extractedAllergens = targetLines.flatMap { line ->
            Allergen.entries.filter { allergen ->
                // Allergen Enum에 등록된 ocr 키워드를 필터링 조건으로 활용
                allergen.ocrKeywords.any { keyword -> line.contains(keyword) }
            }
        }.toSet()
        return extractedAllergens
    }
}