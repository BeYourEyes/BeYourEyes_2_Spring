package com.dna.extensions

fun String.hasBatchim(): Boolean {
    val lastChar = this.last()
    val unicode = lastChar.toInt()
    return (unicode - 0xAC00) % 28 != 0
}

fun String.addSubjectMarker(): String {
    if (this.isBlank()) return this // 빈 문자열이면 마커 추가 안함
    return if (this.hasBatchim()) "${this}이" else "${this}가"
}