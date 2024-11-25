package com.dna.beyoureyes.ui.myInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentMyInfoBinding
import com.dna.beyoureyes.model.FoodHistory


class MyInfoFragment : Fragment() {
    private var _binding: FragmentMyInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FoodHistoryAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.historyRecyclerView
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager // 리사이클러 뷰 레이아웃 매니저 설정

        // 표시할 영양소 데이터 -> 추후 뷰모델이 담당하도록 수정
        val items = mutableListOf(FoodHistory("오후 2시 45분", 170, R.drawable.tmp_food_history))
        adapter = FoodHistoryAdapter(items)
        recyclerView.adapter = adapter // 리사이클러 뷰 어댑터 설정

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}