package com.dna.beyoureyes.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.ResultEatDialogBinding

class CustomDialog(
    private val msg: String,
    private val buttonCallback: (() -> Unit)? = null,
    private val buttonText: String? = null,
    private val secondaryButtonCallback: (() -> Unit)? = null,
    private val secondaryButtonText: String? = null,
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)

        // LayoutInflater를 사용하여 레이아웃 인플레이트
        val dialogBinding = ResultEatDialogBinding.inflate(LayoutInflater.from(requireContext()))

        // 다이얼로그 뷰 설정
        builder.setView(dialogBinding.root)

        // 표시 텍스트(메세지) 설정
        dialogBinding.resultEatDialog.text = msg

        // 다이얼로그 내부 버튼(기본) 텍스트 설정
        buttonText?.let { dialogBinding.button.text = it }

        // 다이얼로그 내부 버튼(기본) 리스너 설정
        dialogBinding.button.setOnClickListener {
            dismiss() // 다이얼로그 닫기
            buttonCallback?.invoke()

        }

        // 두번째 버튼(하단) 활성화(기본은 숨김 처리)
        if (secondaryButtonCallback== null && secondaryButtonText == null) {
            dialogBinding.secondaryButton.visibility = View.GONE
        } else {
            dialogBinding.secondaryButton.visibility = View.VISIBLE
        }

        // 두번째 버튼(하단) 텍스트 설정
        secondaryButtonText?.let { dialogBinding.secondaryButton.text = it }

        // 두번째 버튼(하단) 리스너 설정
        dialogBinding.secondaryButton.setOnClickListener {
            dismiss()
            secondaryButtonCallback?.invoke()
        }

        return builder.create()
    }

}