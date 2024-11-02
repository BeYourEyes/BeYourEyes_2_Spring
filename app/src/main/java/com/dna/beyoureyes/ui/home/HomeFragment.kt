package com.dna.beyoureyes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dna.beyoureyes.databinding.FragmentHomeBinding
import com.dna.beyoureyes.model.Carbs
import com.dna.beyoureyes.model.Cholesterol
import com.dna.beyoureyes.model.Fat
import com.dna.beyoureyes.model.Natrium
import com.dna.beyoureyes.model.Protein
import com.dna.beyoureyes.model.SaturatedFat
import com.dna.beyoureyes.model.Sugar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IntakeAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.todayNutriRecyclerView
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager // 리사이클러 뷰 레이아웃 매니저 설정

        // 표시할 영양소 데이터 -> 추후 뷰모델이 담당하도록 수정
        val items = mutableListOf(
            Carbs(100*1000), Sugar(45*1000),
            Protein(40*1000), Natrium(1700), Cholesterol(420),
            Fat(50*1000), SaturatedFat(15*1000)
        )
        adapter = IntakeAdapter(items, 20, false)
        recyclerView.adapter = adapter // 리사이클러 뷰 어댑터 설정

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}