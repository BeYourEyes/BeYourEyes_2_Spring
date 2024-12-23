package com.dna.beyoureyes.ui.assign

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

// 사용자 정보 등록 & 수정용 프래그먼트 추상화 클래스
abstract class AssignFragment : Fragment() {
    abstract val announceForAccessibilityMsg: String // 프래그먼트 전환 시 읽어줄 스크린 리더용 메세지
    abstract val questionMsg: String // 질문 문자열
    abstract fun isInputValid(): Boolean // 유효성 검사 처리

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 프래그먼트 뷰 초기화 완료 시, 액티비티의 STEP 안내 메세지 및 버튼 텍스트 갱신
        (activity as? AssignActivity)?.updateTextForEachStep(questionMsg)
        view.post {
            view.announceForAccessibility( // 교체 시 스크린 리더에 안내
                announceForAccessibilityMsg
                    .replace("\n", " ")
            )
        }
    }
}