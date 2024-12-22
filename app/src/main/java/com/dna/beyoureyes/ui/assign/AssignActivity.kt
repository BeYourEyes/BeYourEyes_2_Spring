package com.dna.beyoureyes.ui.assign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.ActivityAssignBinding
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.model.Disease
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.model.UserInfo
import com.dna.beyoureyes.ui.CustomToolbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssignActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssignBinding

    private var name : String? = null
    private var gender : Int? = null
    private var birth : String? = null
    private var disease : MutableSet<Disease>? = null
    private var allergens : MutableSet<Allergen>? = null

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
            when(val curFragment = fragmentList[currentStep]) { // 현재 표시한 프래그먼트
                is AssignNameFragment -> {
                    curFragment.getValidInput()?.let { // 유효한 값 가져올 수 있으면
                        name = it // 값 등록
                        moveToNextStep() // 다음 스텝으로
                    }
                }
                is AssignGenderFragment -> {
                    curFragment.getValidInput()?.let { // 유효한 값 가져올 수 있으면
                        gender = it // 값 등록
                        moveToNextStep() // 다음 스텝으로
                    }
                }
                is AssignBirthFragment -> {
                    birth = curFragment.getValidInput()
                    moveToNextStep() // 다음 스텝으로
                }
                is AssignDiseaseFragment -> {
                    disease = curFragment.getValidInput()
                    moveToNextStep() // 다음 스텝으로
                }
                is AssignAllergyFragment -> {
                    allergens = curFragment.getValidInput()
                    moveToNextStep() // 다음 스텝으로
                }
            }
        }

        // 프래그먼트 뷰 초기화(표시할 프래그먼트 리스트의 첫번째 요소로)
        val initFragment = fragmentList[currentStep]
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.assignFragmentContainerView, initFragment)
        fragmentTransaction.commit() // 프래그먼트 교체
        initFragment.view?.announceForAccessibility( // 교체 시 스크린 리더에게 화면 설명 읽어줌
            buildString {
                append("${binding.stepText.text}: ") // e.g. 스텝 1
                append(initFragment.announceForAccessibilityMsg) // 프래그먼트별로 설정된 메세지
            }
        )
    }

    override fun onBackPressed() {
        if (currentStep == 0) {
            finish()
        } else {
            currentStep--
            replaceFragment(fragmentList[currentStep])
        }
        super.onBackPressed()
    }

    private fun moveToNextStep() {
        currentStep++
        if (currentStep in fragmentList.indices) {
            replaceFragment(fragmentList[currentStep])
        } else { // 마지막 단계에서 다음으로 클릭 시
            when (editMode) {
                AssignMode.REGISTER -> {
                    registerInfo()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    updateUserInfo()
                }
            }
            finish()
        }
    }

    fun updateTextForEachStep() {
        val fragment = fragmentList.getOrNull(currentStep)
        if (fragment != null && fragment.isAdded) {
            binding.stepText.text = "STEP ${currentStep + 1}"
            binding.questionText.text = fragment.questionMsg // Fragment의 questionMsg
            if (fragmentList.lastIndex == currentStep) {
                if (editMode == AssignMode.REGISTER)
                    binding.nextBtn.text = "시작하기"
                else
                    binding.nextBtn.text = "저장하기"
            }
        }
    }

    private fun replaceFragment(fragment: AssignFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.assignFragmentContainerView, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit() // 프래그먼트 교체
        fragment.view?.announceForAccessibility( // 교체 시 스크린 리더에게 화면 설명 읽어줌
            buildString {
                append("${binding.stepText.text}: ") // e.g. 스텝 1
                append(fragment.announceForAccessibilityMsg) // 프래그먼트별로 설정된 메세지
            }
        )
    }

    private fun registerInfo() {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val birthDate = dateFormat.parse(birth ?: "") ?: Date()
        val birthTimeStamp = Timestamp(birthDate)

        AppUser.info =
            UserInfo(name ?: "", gender ?: 0, birthTimeStamp,
                disease?.ifEmpty { null }, allergens?.ifEmpty { null })

        val userInfo = hashMapOf(
            "userId" to Firebase.auth.currentUser?.uid,
            "userName" to name!!,
            "userGender" to gender,
            "userBirth" to birthTimeStamp,
            "userProfile" to "", // 일단 빈 값 전송
            "lastActivationDate" to FieldValue.serverTimestamp()
        )
        disease?.let { diseaseSet ->
            if (diseaseSet.isNotEmpty()) { // 질환 정보 전달 - enum명으로 DB 저장
                userInfo["userDisease"] = diseaseSet.map{ it.name }
            }
        }
        allergens?.let { allergenSet ->
            if (allergenSet.isNotEmpty()) { // 알레르기 정보 전달 - enum명으로 DB 저장
                userInfo["userAllergens"] = allergenSet.map{ it.name }
            }
        }
        FirebaseHelper.sendData(userInfo, "userInfo")
    }

    private fun updateUserInfo() {
        val updatedInfo = HashMap<String, Any?>()

        name?.let { name ->
            AppUser.info?.name = name
            updatedInfo["userName"] = name
        }
        gender?.let { gender ->
            AppUser.info?.gender = gender
            updatedInfo["userGender"] = gender
        }
        birth?.let { birth ->
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val birthDate = dateFormat.parse(birth) ?: Date()
            val birthTimeStamp = Timestamp(birthDate)
            AppUser.info?.birth = birthTimeStamp
            updatedInfo["userBirth"] = birthTimeStamp
        }
        disease?.let { diseaseSet ->
            if (diseaseSet.isNotEmpty()) { // 질환 정보 전달 - enum명으로 DB 저장
                AppUser.info?.disease = diseaseSet
                updatedInfo["userDisease"] = diseaseSet.map{ it.name }
            }else { // 없음 선택 시
                AppUser.info?.disease = null
                updatedInfo["userDisease"] = emptyList<String>()

            }
        }
        allergens?.let { allergenSet ->
            if (allergenSet.isNotEmpty()) { // 알레르기 정보 전달 - enum명으로 DB 저장
                AppUser.info?.allergens = allergenSet
                updatedInfo["userAllergens"] = allergenSet.map{ it.name }
            }else {// 없음 선택 시
                AppUser.info?.allergens = null
                updatedInfo["userDisease"] = emptyList<String>()
            }
        }
        AppUser.id?.let { uid ->
            lifecycleScope.launch {
                FirebaseHelper.updateUserData(uid, updatedInfo)
            }
        }
    }
}