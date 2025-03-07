package com.dna.beyoureyes.data.model

// 총 19개
// displayName은 칩에 표시할 한글명
// ocrKeywords는 ocr 분석 시 검색할 키워드 정보(그간의 표시 기준 변화로 인해 여러 개의 키워드 고려 필요)
enum class Allergen(val displayName: String, val ocrKeywords: List<String>) {
    BUCKWHEAT("메밀", listOf("메밀")),
    WHEAT("밀", listOf("밀")),
    SOYBEAN("대두", listOf("대두")),
    PEANUT("땅콩", listOf("땅콩")),
    WALNUT("호두", listOf("호두")),
    PINE_NUT("잣", listOf("잣")),
    SULFUR_DIOXIDE("아황산류", listOf("아황산류")),
    PEACH("복숭아", listOf("복숭아")),
    TOMATO("토마토", listOf("토마토")),
    EGG("난류", listOf("난류", "계란")), // 가금류 한함
    MILK("우유", listOf("우유")),
    SHRIMP("새우", listOf("새우")),
    MACKEREL("고등어", listOf("고등어")),
    SQUID("오징어", listOf("오징어")),
    CRAB("게", listOf("게")),
    SHELLFISH("조개류", listOf("조개류", "굴", "전복", "홍합")), // 굴, 전복, 홍합 포함
    PORK("돼지고기", listOf("돼지고기")),
    BEEF("쇠고기", listOf("쇠고기")),
    CHICKEN("닭고기", listOf("닭고기"))
}