package com.dna.beyoureyes.ui.myInfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.model.Disease
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.model.UserInfoEditMode
import com.dna.beyoureyes.ui.FragmentNavigationListener
import com.dna.beyoureyes.ui.assign.AssignAllergyFragment
import com.dna.beyoureyes.ui.assign.AssignBirthFragment
import com.dna.beyoureyes.ui.assign.AssignDiseaseFragment
import com.dna.beyoureyes.ui.assign.AssignGenderFragment
import com.dna.beyoureyes.ui.assign.AssignNameFragment
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditUserInfoActivity : AppCompatActivity(), FragmentNavigationListener {
    private var name : String? = null
    private var gender : Int? = null
    private var birth : String? = null
    private var currentStep = 0
    private var disease : MutableSet<Disease> = mutableSetOf()
    private var allergens : MutableSet<Allergen> = mutableSetOf()
    private var profile : String = ""
    private lateinit var fragmentList : List<Fragment>

    private val profileFragments = listOf( // 프로필 정보 수정 시 필요한 프래그먼트들
        AssignNameFragment(),
        AssignGenderFragment(),
        AssignBirthFragment()
    )

    private val diseaseFragments = listOf( // 질환 정보 수정 시 필요한 프래그먼트
        AssignDiseaseFragment()
    )

    private val allergyFragments = listOf( // 알레르기 정보 수정 시 필요한 프래그먼트
        AssignAllergyFragment()
    )

    override fun onNavigateToFragment(fragment: Fragment) {
        replaceFragment(fragment)
    }

    override fun onNameInputRecieved(name: String) {
        this.name = name
    }

    override fun onGenderInputRecieved(gender: Int) {
        this.gender = gender
    }

    override fun onBirthInputRecieved(birth: String) {
        this.birth = birth
    }

    override fun onDiseaseInputRecieved(userDiseaseSet: MutableSet<Disease>) {
        this.disease = userDiseaseSet
    }

    override fun onAllergyInputRecieved(userAllergySet: MutableSet<Allergen>) {
        this.allergens = userAllergySet
    }

    override fun onBackPressed() {
        currentStep--
        if (currentStep < 0) {
            finish()
        } else {
            replaceFragment(fragmentList[currentStep])
        }
        super.onBackPressed()
    }

    override fun onBtnClick(currentFragment: Fragment, isNxt : Boolean) {
        if (isNxt) {
            currentStep++
        }
        else {
            currentStep--
        }

        if (currentStep in fragmentList.indices) {
            replaceFragment(fragmentList[currentStep])
        } else {
            updateUserInfo()
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign)

        // 수정해야 할 정보가 프로필/질환/알레르기 중 무엇인지 가져옴
        val editMode = intent.getSerializableExtra("EDIT_MODE") as? UserInfoEditMode

        fragmentList = when (editMode) {
            UserInfoEditMode.PROFILE -> {
                currentStep = 0
                profileFragments
            }
            UserInfoEditMode.DISEASE -> {
                currentStep = 0
                diseaseFragments
            }
            UserInfoEditMode.ALLERGY -> {
                currentStep = 0
                allergyFragments
            }
            else -> {
                finish()
                return
            }
        }
        replaceFragment(fragmentList[currentStep])
    }

    private fun updateUserInfo() {
        val updatedInfo = HashMap<String, Any?>()

        if (!name.isNullOrBlank()) {
            AppUser.info?.name = name!!
            updatedInfo["userName"] = name!!
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
        if (disease.isNotEmpty()) { // 질환 정보 전달 - enum명으로 DB 저장
            AppUser.info?.disease = disease
            updatedInfo["userDisease"] = disease.map{ it.name }
        }
        if (allergens.isNotEmpty()) { // 알레르기 정보 전달 - enum명으로 DB 저장
            AppUser.info?.allergens = allergens
            updatedInfo["userAllergens"] = allergens.map{ it.name }
        }
        AppUser.id?.let { uid ->
            lifecycleScope.launch {
                FirebaseHelper.updateUserData(uid, updatedInfo)
            }
        }
    }
}