package com.dna.beyoureyes.ui.foodAnalysis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentViewHolder
import com.dna.beyoureyes.R

class FoodResultAdapter(
    private val fragmentManager: FragmentManager,
    private val fragments: List<Fragment>
) : RecyclerView.Adapter<FoodResultAdapter.FoodResultViewHolder>(){

    class FoodResultViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_result, parent, false)
        return FoodResultViewHolder(view)
    }


    override fun onBindViewHolder(holder: FoodResultViewHolder, position: Int) {
        val fragmentContainer = holder.itemView.findViewById<FrameLayout>(R.id.fragmentContainer)

        // 기존 Fragment가 있으면 제거
        val existingFragment = fragmentManager.findFragmentByTag("fragment_$position")
        if (existingFragment != null) {
            fragmentManager.beginTransaction().remove(existingFragment).commit()
        }

        // 새 Fragment를 추가
        val fragment = fragments[position]
        fragmentManager.beginTransaction()
            .replace(fragmentContainer.id, fragment, "fragment_$position")
            .commit()
    }

    override fun getItemCount(): Int = fragments.size
}