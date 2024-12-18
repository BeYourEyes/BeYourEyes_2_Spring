package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.model.UserInfo
import com.dna.beyoureyes.ui.FragmentNavigationListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssignActivity : AppCompatActivity(), FragmentNavigationListener {
    private var name : String? = null
    private var gender : Int? = null
    private var birth : String? = null
    private var currentStep = 0
    private var disease : ArrayList<String> = ArrayList<String>()
    private var allergy : ArrayList<String> = ArrayList<String>()
    private var allergens : MutableSet<Allergen> = mutableSetOf()
    private var profile : String = ""

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

    override fun onDiseaseInputRecieved(userDiseaseList: ArrayList<String>) {
        this.disease = userDiseaseList
    }

    override fun onAllergyInputRecieved(userAllergySet: MutableSet<Allergen>) {
        this.allergens = userAllergySet
    }

    override fun onBackPressed() {
        currentStep--
        super.onBackPressed()
    }

    override fun onBtnClick(currentFragment: Fragment, isNxt : Boolean) {
        if (isNxt) {
            currentStep++
        }
        else {
            currentStep--
        }

        when(currentStep) {
            0 -> replaceFragment(AssignNameFragment())
            1 -> replaceFragment(AssignGenderFragment())
            2 -> replaceFragment(AssignBirthFragment())
            3 -> replaceFragment(AssignDiseaseFragment())
            4 -> replaceFragment(AssignAllergyFragment())
            5 -> {
                // currentStep이 5 이상일 경우 MainActivity로 전환
                registInfo()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()  // 현재 Activity 종료
            }
            else -> {
                finish()
            }
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

        replaceFragment(AssignNameFragment())
    }

    fun registInfo() {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val birthDate = dateFormat.parse(birth ?: "") ?: Date()
        val birthTimeStamp = Timestamp(birthDate)
        AppUser.info = UserInfo(name?:"", gender?:0, birthTimeStamp, disease, allergens.ifEmpty { null })
        val userInfo = hashMapOf(
            "userId" to Firebase.auth.currentUser?.uid,
            "userName" to name!!,
            "userGender" to gender,
            "userBirth" to birthTimeStamp,
            "userDisease" to disease,
            "userAllergy" to allergy,
            "userProfile" to profile,
            "lastActivationDate" to FieldValue.serverTimestamp()
        )
        if (allergens.isNotEmpty()) {
            userInfo["userAllergens"] = allergens.map{ it.name }
        }
        FirebaseHelper.sendData(userInfo, "userInfo")
    }

}