package com.dna.beyoureyes.data.api.response

/**
 * Spring API 응답을 공통적으로 감싸는 데이터 클래스(제네릭) 정의
 *
 * 응답 속성 중 data의 형식은 API별로 다르므로 제네릭 활용
 */
data class SpringApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?
)
