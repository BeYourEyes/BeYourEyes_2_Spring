package com.dna.beyoureyes.ui.assign

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignNameBinding
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener
import kotlinx.coroutines.launch

class AssignNameFragment : Fragment() {

    private var isDuplicateNickname = 0


    private lateinit var binding: FragmentAssignNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssignNameBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val listener = activity as? FragmentNavigationListener
        binding.nextBtn.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 입력해주세요.", Toast.LENGTH_LONG).show()
            }
            else {
                when(isDuplicateNickname) {
                    -1 -> {
                        Toast.makeText(requireContext(), "중복 검사를 해주세요.", Toast.LENGTH_LONG).show()
                    }
                    0 -> {
                        Toast.makeText(
                            requireContext(),
                            "중복된 이름입니다. 다른 이름을 사용해주세요.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    1 -> {
                        listener?.onNameInputRecieved(binding.nameInput.text.toString())
                        listener?.onBtnClick(this, true)
                    }
                    else -> {
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }


        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                listener?.onBtnClick(this@AssignNameFragment, false)
            }
        }

        binding.delBtn.setOnClickListener {
            binding.nameInput.text = null
        }

        binding.searchIcon.setOnClickListener {
            lifecycleScope.launch {
                val isDuplicate =
                    FirebaseHelper.checkDuplicateName(binding.nameInput.text.toString().trim())
                if (isDuplicate) {
                    isDuplicateNickname = 0
                    binding.validationText.setText("중복된 이름입니다. 다시 설정해주세요!")
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
                else {
                    isDuplicateNickname = 1
                    binding.validationText.setText("사용 가능한 닉네임입니다!")
                    binding.validationText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue_500
                        )
                    )
                }
            }
        }
        /*
        binding.nameInput.setOnEditorActionListener { v, actionId, event ->
            // 엔터키가 눌러졌을 때
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                // 키보드를 내린 후 중복 검사 실행
                val nickname = v.text.toString().trim()
                if (nickname.isNotEmpty()) {
                    lifecycleScope.launch {
                        val isDuplicate = FirebaseHelper.checkDuplicateName(nickname)
                        if (isDuplicate) {
                            isDuplicateNickname = 0
                            binding.validationText.setText("중복된 이름입니다. 다시 설정해주세요!")
                            binding.validationText.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_500))
                            YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .delay(0)
                                .playOn(binding.validationText)
                        } else {
                            isDuplicateNickname = 1
                            binding.validationText.setText("사용 가능한 닉네임입니다.")
                            binding.validationText.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_500))
                        }
                    }
                }
                true // true를 반환하여 기본 엔터키 동작을 막음
            } else {
                false // 엔터 외의 다른 키 이벤트는 처리하지 않음
            }
        }
        */

        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 중
            }

            override fun afterTextChanged(editable: Editable?) {
                // 텍스트가 변경된 후에 자동으로 중복 검사
                val nickname = editable.toString().trim()
                if (nickname.isNotEmpty()) {
                    // 비동기적으로 중복 검사
                    lifecycleScope.launch {
                        val isDuplicate = FirebaseHelper.checkDuplicateName(nickname)
                        if (isDuplicate) {
                            isDuplicateNickname = 0
                            binding.validationText.setText("중복된 이름입니다. 다시 설정해주세요!")
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
                        } else {
                            isDuplicateNickname = 1
                            binding.validationText.setText("사용 가능한 닉네임입니다.")
                            binding.validationText.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.blue_500
                                )
                            )
                        }
                    }
                }
            }
        })


        return binding.root
    }

}