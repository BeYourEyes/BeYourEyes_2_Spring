package com.dna.beyoureyes.ui.myInfo

import android.content.Intent
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

    private val shareTitle = "친구에게 공유하기"
    private val appLink = "https://play.google.com/store/apps/details?id=com.dna.beyoureyes"
    private val content = "더 건강한 식습관으로 당신의 삶을 변화시키세요!\n" + "지금 바로 앱을 다운로드하세요 ⬇️"

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

        // 앱 공유하기 버튼
        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "$content\n\n$appLink")
            startActivity(Intent.createChooser(intent, shareTitle))

        }
        // 앱 스토어 리뷰 남기기
        binding.reviewBtn.setOnClickListener {

        }
        // 고객센터 문의하기
        binding.contactBtn.setOnClickListener {

        }
        // 개인정보 처리 방침
        binding.privacyPolicyBtn.setOnClickListener {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}