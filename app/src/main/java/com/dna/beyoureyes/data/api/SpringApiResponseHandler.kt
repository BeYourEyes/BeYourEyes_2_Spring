package com.dna.beyoureyes.data.api

import android.util.Log
import com.dna.beyoureyes.data.api.model.ApiStatus
import com.dna.beyoureyes.data.api.model.AuthException
import com.dna.beyoureyes.data.api.response.SpringApiResponse
import retrofit2.Response

/**
 * 공통 응답 구조를 가진 Spring API Response를 처리하는 Handler 클래스
 *
 * Spring 서버 Api들은 공통 응답구조를 갖고 있기 때문에
 * 통신 성공 여부 등을 전용 핸들러로 공통 관리
 * 응답에 포함된 data의 형식이 API별로 다르므로 data 표현에 제네릭 활용
 *
 * Kotlin의 DSL 스타일 API 및 확장 함수로 정의
 * 체이닝 스타일로 개선하여 가독성과 호출 시 직관성을 높이고자 함.
 */

class SpringApiResponseHandler<T>(private val call: suspend () -> Response<SpringApiResponse<T>>) {

    // 요청 성공 시 실행할 콜백 (data와 status 전달)
    private var onSuccess: (suspend (T?, ApiStatus) -> Unit)? = null
    fun onSuccess(callback: suspend (T?, ApiStatus) -> Unit): SpringApiResponseHandler<T> {
        this.onSuccess = callback
        return this
    }

    // 요청 실패 시 실행할 콜백 (실패 메시지와 status 전달)
    private var onError: (suspend (ApiStatus) -> Unit)? = null
    fun onError(callback: suspend (ApiStatus) -> Unit): SpringApiResponseHandler<T> {
        this.onError = callback
        return this
    }

    // 응답 요청 공통 로직
    suspend fun execute() {
        try {
            val response = call()  // API 요청
            if (response.isSuccessful) { // http code 200 ~ 299일 경우(204나 205는 제외)
                response.body()?.let { apiResponse ->
                    // 정상 응답을 받은 경우
                    Log.d("SPRING_API_SUCCESS", apiResponse.message)
                    val status = apiResponse.getApiStatus() // status 값 파싱
                    onSuccess?.invoke(apiResponse.data, status)  // status별 처리는 콜백에 맡기기
                } ?: run {
                    // 응답 body가 null인 경우
                    Log.e("SPRING_API_ERROR", "Null Response Body")
                    onError?.invoke(ApiStatus.UNKNOWN)
                }
            } else {
                // API 요청이 실패한 경우
                val errorMessage = response.errorBody()?.string() ?: "Server Error"
                Log.e("SPRING_API_ERROR", errorMessage)
                onError?.invoke(ApiStatus.SERVER_ERROR)
            }
        } catch (e: AuthException) { // 액세스 토큰 만료 되었는데, 갱신 실패한 경우
            Log.e("SPRING_API_ERROR", "Auth Exception: ${e.message}")
            onError?.invoke(ApiStatus.SERVER_ERROR)
        } catch (e: Exception) { // 기타 오류
            Log.e("SPRING_API_ERROR", "Exception: ${e.message}")
            onError?.invoke(ApiStatus.NETWORK_ERROR)
        }
    }
}