package com.dna.beyoureyes.ui

import androidx.fragment.app.Fragment
import com.dna.beyoureyes.model.Allergen

interface FragmentNavigationListener {
    fun onNavigateToFragment(fragment: Fragment)
    fun  onBtnClick(currentFragment: Fragment, isNxt : Boolean)
    fun onNameInputRecieved(name: String)
    fun onGenderInputRecieved(gender : Int)
    fun onBirthInputRecieved(birth : String)
    fun onDiseaseInputRecieved(userDiseaseList : ArrayList<String>)
    fun onAllergyInputRecieved(userAllergySet : MutableSet<Allergen>)
}