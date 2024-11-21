package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dna.beyoureyes.ui.FragmentNavigationListener

class AssignActivity : AppCompatActivity(), FragmentNavigationListener {

    private var currentStep = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign)

        replaceFragment(AssignNameFragment())
    }

    override fun onNavigateToFragment(fragment: Fragment) {
        replaceFragment(fragment)
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

}