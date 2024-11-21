package com.dna.beyoureyes.ui

import androidx.fragment.app.Fragment

interface FragmentNavigationListener {
    fun onNavigateToFragment(fragment: Fragment)

    fun  onBtnClick(currentFragment: Fragment, isNxt : Boolean)
}