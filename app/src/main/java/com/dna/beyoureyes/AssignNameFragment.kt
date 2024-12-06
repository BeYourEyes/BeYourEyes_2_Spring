package com.dna.beyoureyes

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dna.beyoureyes.databinding.ActivityAssignBinding
import com.dna.beyoureyes.databinding.FragmentAssignNameBinding
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var isDuplicateNickname = 0

/**
 * A simple [Fragment] subclass.
 * Use the [AssignNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssignNameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAssignNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
                        Toast.makeText(requireContext(), "중복된 이름입니다. 다른 이름을 사용해주세요.", Toast.LENGTH_LONG).show()
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


        binding.toolbar.backButtonClickListener = object : CustomToolbar.BackButtonClickListener {
            override fun onBackButtonClicked() {
                listener?.onBtnClick(this@AssignNameFragment, false)
            }
        }

        binding.delBtn.setOnClickListener {
            binding.nameInput.text = null
        }

        binding.searchIcon.setOnClickListener {
            lifecycleScope.launch {
                val isDuplicate = FirebaseHelper.checkDuplicateName(binding.nameInput.text.toString().trim())
                if (isDuplicate) {
                    isDuplicateNickname = 0
                    binding.validationText.setText("중복된 이름입니다. 다시 설정해주세요!")
                    binding.validationText.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_500))
                    YoYo.with(Techniques.Shake)
                        .duration(700)
                        .delay(0)
                        .playOn(binding.validationText)
                }
                else {
                    isDuplicateNickname = 1
                    binding.validationText.setText("사용 가능한 닉네임입니다!")
                    binding.validationText.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_500))
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
                            binding.validationText.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_500))
                            YoYo.with(Techniques.Shake)
                                .duration(700)
                                .delay(0)
                                .playOn(binding.validationText)
                        } else {
                            isDuplicateNickname = 1
                            binding.validationText.setText("사용 가능한 닉네임입니다.")
                            binding.validationText.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_500))
                        }
                    }
                }
            }
        })


        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AssignNameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AssignNameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}