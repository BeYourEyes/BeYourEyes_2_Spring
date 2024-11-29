package com.dna.beyoureyes.ui.myInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.databinding.FragmentMyInfoBinding

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

        val activity = requireActivity() as MainActivity
        adapter = FoodHistoryAdapter(activity.foodHistoryItems)
        recyclerView.adapter = adapter // 리사이클러 뷰 어댑터 설정
        binding.historyCnt.text = "${adapter.itemCount}"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}