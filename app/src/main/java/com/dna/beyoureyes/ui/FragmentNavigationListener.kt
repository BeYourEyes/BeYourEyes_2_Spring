package com.dna.beyoureyes.ui

import androidx.fragment.app.Fragment

interface FragmentNavigationListener {
    fun onNavigateToFragment(fragment: Fragment)

    fun  onBtnClick(currentFragment: Fragment, isNxt : Boolean)

    fun onNameInputRecieved(name: String)

    fun onGenderInputRecieved(gender : Int)

    fun onBirthInputRecieved(birth : Int)

    fun onDiseaseInputRecieved(userDiseaseList : ArrayList<String>)

    fun onAllergyInputRecieved(userAllergyList : ArrayList<String>)

}