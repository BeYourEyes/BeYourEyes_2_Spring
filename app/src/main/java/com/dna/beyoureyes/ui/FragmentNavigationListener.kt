package com.dna.beyoureyes.ui

import androidx.fragment.app.Fragment
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.model.Disease

interface FragmentNavigationListener {
    fun onNavigateToFragment(fragment: Fragment)
    fun  onBtnClick(currentFragment: Fragment, isNxt : Boolean)
    fun onNameInputRecieved(name: String)
    fun onGenderInputRecieved(gender : Int)
    fun onBirthInputRecieved(birth : String)
    fun onDiseaseInputRecieved(userDiseaseSet : MutableSet<Disease>)
    fun onAllergyInputRecieved(userAllergySet : MutableSet<Allergen>)
}