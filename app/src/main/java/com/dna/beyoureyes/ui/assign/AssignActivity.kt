package com.dna.beyoureyes.ui.assign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.R
import com.dna.beyoureyes.data.api.model.ApiStatus
import com.dna.beyoureyes.databinding.ActivityAssignBinding
import com.dna.beyoureyes.ui.common.CustomToolbar
import com.dna.beyoureyes.data.repository.AuthRepositoryImpl
import com.dna.beyoureyes.data.local.TokenManager
import com.dna.beyoureyes.ui.common.CustomDialog
import kotlinx.coroutines.launch


class AssignActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssignBinding
    private val viewModel : AssignViewModel by viewModels()

    // 액세스 토큰 저장소 세팅
    private val tokenManager = TokenManager(this)
    private val authRepository = AuthRepositoryImpl(tokenManager)

    private var editMode: AssignMode? = null // 정보 수정 모드 관리를 위한 속성
    private var currentStep = 0
    private lateinit var fragmentList : List<AssignFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 바인딩
        binding = ActivityAssignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 정보 수정 모드 확인 (전부 다 등록/ 프로필만 수정/ 질환만 수정/ 알레르기만 수정)
        editMode = intent.getSerializableExtra("EDIT_MODE") as? AssignMode
        currentStep = 0
        fragmentList = when (editMode) {
            AssignMode.PROFILE -> {
                binding.toolbar.setTitle("내 정보 수정")
                listOf( // 프로필 정보 수정 시 필요한 프래그먼트들
                    AssignNameFragment(),
                    AssignGenderFragment(),
                    AssignBirthFragment()
                )
            }
            AssignMode.DISEASE -> {
                binding.toolbar.setTitle("내 질환 수정")
                listOf( // 질환 정보 수정 시 필요한 프래그먼트
                    AssignDiseaseFragment()
                )
            }
            AssignMode.ALLERGY -> {
                binding.toolbar.setTitle("내 알레르기 수정")
                listOf( // 알레르기 정보 수정 시 필요한 프래그먼트
                    AssignAllergyFragment()
                )
            }
            AssignMode.REGISTER -> {
                listOf( // 회원가입 시 필요한 프래그먼트
                    AssignNameFragment(),
                    AssignGenderFragment(),
                    AssignBirthFragment(),
                    AssignDiseaseFragment(),
                    AssignAllergyFragment()
                )
            }
            else -> {
                finish()
                return
            }
        }

        // 뒤로가기 버튼 클릭 리스너
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                onBackPressed()
            }
        }

        // 다음으로 버튼 클릭 리스너 설정
        binding.nextBtn.setOnClickListener {
            if (fragmentList[currentStep].isInputValid()) { // input 유효성 검사 성공하면
                moveToNextStep() // 다음 스텝으로
            }
        }

        // 프래그먼트 뷰 초기화(표시할 프래그먼트 리스트의 첫번째 요소로)
        replaceFragment(fragmentList[currentStep])
    }

    override fun onBackPressed() {
        // 백 스택에 Fragment가 있는지 확인
        if (supportFragmentManager.backStackEntryCount <= 1) {
            // 첫 번째 Fragment인 경우 프래그먼트 더 빼지 않고 액티비티 종료
            finish()
        } else {
            // 첫 번째 Fragment가 아닌 경우, 이전 Fragment로 돌아감
            currentStep--
            super.onBackPressed()
        }
    }

    private fun moveToNextStep() {
        currentStep++
        if (currentStep in fragmentList.indices) {
            // 마지막 단계가 아닐 시
            replaceFragment(fragmentList[currentStep])
        } else {
            // 마지막 단계일 시
            when (editMode) {
                AssignMode.REGISTER -> { // 정보 최초 등록 시
                    try {
                        val intent = Intent(this, MainActivity::class.java)
                        lifecycleScope.launch { // 가입 시도 후 결과 상태 받기
                            val status = viewModel.registerUserInfo(authRepository)
                            exceptionHandlingAfterApiCall(
                                status = status,
                                onSuccess = { startActivity(intent) }
                            )
                        }
                    }catch(e: IllegalArgumentException){ // 가입 중 argument 처리 오류 발생 시
                        Log.e("REGISTER_ERROR", "$e")
                        // -> 오류 처리
                    }
                } AssignMode.PROFILE -> {
                    lifecycleScope.launch {
                        val status = viewModel.updateProfile(authRepository)
                        exceptionHandlingAfterApiCall(
                            status = status,
                            onSuccess = { setResult(Activity.RESULT_OK) }
                        )
                    }
                } AssignMode.ALLERGY -> {
                    lifecycleScope.launch {
                        val status = viewModel.updateAllergens(authRepository)
                        exceptionHandlingAfterApiCall(
                            status = status,
                            onSuccess = { setResult(Activity.RESULT_OK) }
                        )
                    }
                } AssignMode.DISEASE -> {
                    lifecycleScope.launch {
                        setResult(Activity.RESULT_OK)
                        val status = viewModel.updateDisease(authRepository)
                        exceptionHandlingAfterApiCall(
                            status = status,
                            onSuccess = { setResult(Activity.RESULT_OK) }
                        )
                    }
                } else -> { }
            }
            finish()
        }
    }

    private fun exceptionHandlingAfterApiCall(status:ApiStatus, onSuccess: () -> Unit) {
        when(status) {
            ApiStatus.SUCCESS -> onSuccess.invoke()
            ApiStatus.SERVER_ERROR, ApiStatus.NETWORK_ERROR -> { // 서버 응답 에러 & 기타 오류(아마 네트워크 오류)
                showDialogForServerError()
            } ApiStatus.UNKNOWN -> { // 알 수 없는 오류(null response body?)
                showDialogForUnknownError()
            } else -> {} // 존재 X
        }
    }

    private fun showDialogForServerError() {
        CustomDialog("서버와의 연결에 실패했습니다.\n다시 시도해 봐도 오류가 반복되면\n앱을 다시 시작해 주세요.")
            .show(supportFragmentManager, "Dialog")
    }

    private fun showDialogForUnknownError() {
        CustomDialog("알 수 없는 오류가 발생했습니다.\n다시 시도해 봐도 오류가 반복되면\n앱을 다시 시작해 주세요.")
            .show(supportFragmentManager, "Dialog")
    }

    fun updateTextForEachStep(questionMsg:String) {
        val fragment = fragmentList.getOrNull(currentStep)
        if (fragment != null && fragment.isAdded) {
            binding.stepText.text = "STEP ${currentStep + 1}"
            binding.questionText.text = questionMsg // Fragment의 questionMsg
            if (fragmentList.lastIndex == currentStep) {
                if (editMode == AssignMode.REGISTER)
                    binding.nextBtn.text = "시작하기"
                else
                    binding.nextBtn.text = "저장하기"
            }else {
                binding.nextBtn.text = "다음으로"
            }
        }
    }

    private fun replaceFragment(fragment: AssignFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.assignFragmentContainerView, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit() // 프래그먼트 교체
    }
}