package com.dna.beyoureyes.ui.assign

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignNameBinding

class AssignNameFragment : AssignFragment() {

    private lateinit var binding: FragmentAssignNameBinding
    private val viewModel: AssignViewModel by activityViewModels()

    override val questionMsg: String
        get() = getString(R.string.assign_step1_question)
    override val announceForAccessibilityMsg: String
        get() = questionMsg

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignNameBinding.inflate(inflater, container, false)

        binding.delBtn.setOnClickListener {
            binding.nameInput.text = null
        }

        binding.searchIcon.setOnClickListener {
            viewModel.validateName(binding.nameInput.text.toString().trim())
        }

        binding.nameInput.addTextChangedListener(object : TextWatcher {
            // 텍스트 변경 전
            override fun beforeTextChanged(charSequence: CharSequence?,
                                           start: Int, count: Int, after: Int) {}
            // 텍스트 변경 중
            override fun onTextChanged(charSequence: CharSequence?,
                                       start: Int, before: Int, count: Int) {}
            // 텍스트 변경 후 -> 자동 중복 검사
            override fun afterTextChanged(editable: Editable?) {
                viewModel.validateName(editable.toString().trim())
            }
        })

        viewModel.nameValidationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AssignViewModel.NameValidation.Duplicate,
                is AssignViewModel.NameValidation.Empty -> {
                    binding.validationText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.alert_500
                        )
                    )
                    YoYo.with(Techniques.Shake)
                        .duration(700)
                        .delay(0)
                        .playOn(binding.validationText)
                }
                is AssignViewModel.NameValidation.Valid -> {
                    binding.validationText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue_500
                        )
                    )
                }
            }
        }

        viewModel.nameValidationMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.validationText.text = message
        })

        /*
        binding.nameInput.setOnEditorActionListener { v, actionId, event ->
            // 엔터키가 눌러졌을 때
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                // 키보드를 내린 후 중복 검사 실행
                val nickname = v.text.toString().trim()
                viewModel.validateName(nickname)
                true // true를 반환하여 기본 엔터키 동작을 막음
            } else {
                false // 엔터 외의 다른 키 이벤트는 처리하지 않음
            }
        }

         */

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.name?.let {
            binding.nameInput.setText(it)
        }
    }

    // 입력 내용 유효성 검증 & 입력 내용 getter
    override fun isInputValid(): Boolean {
        val name = binding.nameInput.text.toString().trim()
        viewModel.validateName(name)
        when(viewModel.nameValidationResult.value) {
            null -> {
                Toast.makeText(
                    requireContext(),
                    "중복 검사를 진행해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
            AssignViewModel.NameValidation.Valid -> {
                viewModel.setName(name) // 유효하면 뷰모델에 값 저장
                return true
            }
            AssignViewModel.NameValidation.Empty -> {
                Toast.makeText(
                    requireContext(),
                    "이름을 입력해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
            AssignViewModel.NameValidation.Duplicate -> {
                Toast.makeText(
                    requireContext(),
                    "중복된 이름입니다. 다른 이름을 사용해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "error",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        }
    }
}