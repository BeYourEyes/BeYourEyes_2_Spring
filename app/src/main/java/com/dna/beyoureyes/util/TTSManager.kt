package com.dna.beyoureyes.util

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.model.CaloricNutrient
import com.dna.beyoureyes.model.Food
import java.util.LinkedList
import java.util.Locale

class TTSManager(context: Context) :
    TextToSpeech.OnInitListener,  UtteranceProgressListener() {

    companion object {
        @Volatile
        private var INSTANCE: TTSManager? = null // 싱글톤 패턴 구현(TTS 엔진이 무거워서 최적화를 위해)

        fun getInstance(context: Context): TTSManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TTSManager(context).also { INSTANCE = it }
            }
        }
    }

    private var tts: TextToSpeech? = null   // tts 객체
    private var isSpeaking: Boolean = false // 재생과 일시 정지 전환을 위한 Boolean
    private val utteranceIdQueue = LinkedList<String>() // 음성 출력 큐 관리
    private var ttsStateListener: TTSStateListener? = null // TTS 상태 변경 콜백 인터페이스

    // TTS 상태 변경 콜백 인터페이스 -> UI 실시간 업뎃을 위해
    interface TTSStateListener {
        fun onTTSStarted()  // TTS 음성 시작 시
        fun onTTSDone()     // TTS 음성 종료 시
    }

    init {
        tts = TextToSpeech(context, this).apply {
            setOnUtteranceProgressListener(this@TTSManager) // UtteranceProgressListener 등록
        } // 초기화
    }

    fun setTTSStateListener(listener: TTSStateListener) { // TTS 상태 변경 콜백 설정 함수
        this.ttsStateListener = listener
    }

    override fun onInit(status: Int) { // 초기화 직후 처리
        if (status == TextToSpeech.SUCCESS) { // TTS 초기화 성공 시
            val result = tts?.setLanguage(Locale.KOREAN) // 한국어 설정
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTSManager", "Language is not supported or missing data")
            } else {
                Log.d("TTSManager", "TextToSpeech initialization successful") // 언어 설정 성공
            }
        } else {  // TTS 초기화 실패 시
            Log.e("TTSManager", "TextToSpeech initialization failed")
        }
    }

    private fun speak(text: String) {
        if (tts?.isSpeaking == true) {
            return // 이전 음성 출력이 완료될 때까지 대기
        }
        isSpeaking = true
        ttsStateListener?.onTTSStarted() // 상태 변경 - 음성 출력 시작

        val utteranceId = System.currentTimeMillis().toString()
        utteranceIdQueue.add(utteranceId)

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        tts?.speak(text, TextToSpeech.QUEUE_ADD, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        isSpeaking = false
        utteranceIdQueue.clear()
        ttsStateListener?.onTTSDone()
    }

    fun isSpeaking(): Boolean {
        return isSpeaking
    }

    fun speakNutritionalInfo(food: Food) {
        val calorieMsg: String? = food.kcal?.let {"해당 식품의 칼로리는 $it kcal 입니다. "}

        val allergyMsg: String? = food.allergy?.let {
            "해당 식품에는 ${it.joinToString(", ")} 성분이 함유되어 있습니다. "
        }
        val commonAllergyMsg: String? = food.allergy?.let { foodAllergy ->
            AppUser.info?.findMatchingAllergy(foodAllergy)?.let { commonAllergy -> // 인식 정보 O
                if (commonAllergy.isEmpty()) { // 사용자 - 식품 알러지 교집합 X
                    "당신의 알러지 성분은 함유되어 있지 않네요. "
                } else { // 사용자 - 식품 알러지 교집합 O
                    "주의하세요. 해당 식품에는 당신이 유의해야 할 ${commonAllergy.joinToString()} 성분이 함유되어 있습니다. "
                }
            }
        } ?: run { null } // 인식 정보 X

        val caloricNutrientMsg: String? = food.nutritions?.filterIsInstance<CaloricNutrient>()
            ?.let { caloricNutris ->
                val total = caloricNutris.sumOf { it.kcal }
                val percentTexts = caloricNutris.joinToString(", ")
                    { "${it.name}이 ${(it.kcal.toDouble() / total.toDouble() * 100).toInt()}%" }
                "해당 식품의 에너지 성분 비율은 $percentTexts 입니다. "
        }?: run { null }

        val nutrientMsg: String? = food.nutritions?.joinToString(", ")
        { nutri -> "${nutri.name}은 ${nutri.percentageOfDailyValue}" }

        val analysisFailMsg: String? =
            if (food.allergy == null )
                "사진에서 알레르기 정보는 인식되지 않았어요. "
            else if (food.nutritions == null)
                "사진에서 영양성분표는 인식되지 않았어요. "
            else null

        val textToSpeak = buildString{
            append("당신을 위한 맞춤 식품 분석 결과를 알려드릴게요. ")
            calorieMsg?.let { append(it) }
            allergyMsg?.let { append(it) }
            caloricNutrientMsg?.let { append(it) }
            commonAllergyMsg?.let { append(it) }
            nutrientMsg?.let { append("해당 식품의 상세 영양성분 정보는 당신의 일일 권장량 기준 $it% 입니다. ") }
            analysisFailMsg?.let { append(it) }
            append("등록하기 버튼을 누르면, 먹은 양을 조절해 이 식품에 대한 섭취 기록을 남길 수 있어요.")
        }
        speak(textToSpeak)
    }

    override fun onStart(utteranceId: String?) {
        Log.d("TTSManager", "TTS started: $utteranceId")
    }

    override fun onDone(utteranceId: String?) {
        utteranceIdQueue.remove(utteranceId)
        if (utteranceIdQueue.isEmpty()) {
            isSpeaking = false
            ttsStateListener?.onTTSDone() // 상태 변경 - 음성 출력 정지
        }
    }

    override fun onError(utteranceId: String?) {
        Log.d("TTSManager", "TTS error: $utteranceId")
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isSpeaking = false
        utteranceIdQueue.clear()
        ttsStateListener?.onTTSDone()
    }
}