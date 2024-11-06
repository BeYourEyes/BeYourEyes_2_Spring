package com.dna.beyoureyes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dna.beyoureyes.onboarding.f_Onboarding

class RegisterPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> f_Onboarding.newInstance(1)
            1 -> f_Onboarding.newInstance(2)
            else -> f_Onboarding.newInstance(3)
        }
    }

    override fun getCount() =  3
}