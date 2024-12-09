package com.dna.beyoureyes.ui.myInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentMyInfoBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.google.android.material.chip.Chip
import com.dna.beyoureyes.BuildConfig

class MyInfoFragment : Fragment() {
    private var _binding: FragmentMyInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodHistoryAdapter: FoodHistoryAdapter

    private val shareTitle = "친구에게 공유하기"
    private val appLink = "https://play.google.com/store/apps/details?id=com.dna.beyoureyes"
    private val content = "더 건강한 식습관으로 당신의 삶을 변화시키세요!\n" + "지금 바로 앱을 다운로드하세요 ⬇️"
    private val policyLink = "https://polar-wolf-52e.notion.site/3ff38ff000ef47b99fa8d4e890a6b4a7?pvs=74"
    private val contactLink = "http://pf.kakao.com/_Bdqun/chat"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val activity = requireActivity() as MainActivity
        foodHistoryAdapter = FoodHistoryAdapter(activity.foodHistoryItems) {
                foodHistory ->
            val action = MyInfoFragmentDirections.actionMyInfoToPastDetail(foodHistory)
            findNavController().navigate(action)
        } // 리사이클러 뷰 어댑터 정의 - 데이터 전달 & 아이템 클릭 시 액션 정의(프래그먼트 교체)

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodHistoryAdapter
        }

        binding.historyCnt.text = "${foodHistoryAdapter.itemCount}"

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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contactLink))
            startActivity(intent)
        }
        // 개인정보 처리 방침
        binding.privacyPolicyBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyLink))
            startActivity(intent)
        }
        // 버전 이름
        binding.appVersionText.setText("version ${BuildConfig.VERSION_NAME}")
        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }

        updateProfile()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateProfile() {

        val diseaseMap = mapOf(
            "diabete" to binding.diabete,
            "highblood" to binding.highblood,
            "hyperlipidemia" to binding.hyperlipidemia
        )
        val allergyMap = mapOf(
            "buckwheat" to "메밀",
            "wheat" to "밀",
            "bean" to "대두",
            "peanut" to "땅콩",
            "walnut" to "호두",
            "pinenut" to "잣",
            "acid" to "아황산",
            "peach" to "복숭아",
            "tomato" to "토마토"
        )

        val userDiseases = AppUser.info?.disease?.toSet() ?: emptySet()
        val userAllergy = AppUser.info?.allergic?.toSet() ?: emptySet()


        binding.profileName.setText(AppUser.info?.name?:"")
        diseaseMap.forEach { diseaseName, diseaseButton ->
            if (userDiseases.contains(diseaseName)) {
                diseaseButton.visibility = View.VISIBLE
            } else {
                diseaseButton.visibility = View.GONE
            }
        }
        /*
        val layoutParams = binding.allergyChipGroup.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = 200
        binding.allergyChipGroup.layoutParams = layoutParams
        */


        AppUser.info?.allergic?.forEach {
            if (it != "none") {
                val chip = Chip(requireContext())
                chip.text = allergyMap[it]
                if (chip.text.length == 1) {
                    chip.text = " " + allergyMap[it] + " " // 한글자인 경우에는 width 설정이 안됐음ㅜㅜ...
                }
                val params = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.chip_width), // 85dp
                    resources.getDimensionPixelSize(R.dimen.chip_height) // 40dp
                )
                params.gravity = Gravity.CENTER
                chip.layoutParams = params

                chip.setTextAppearanceResource(R.style.chipTextMyInfo)
                chip.isChecked = true  // 클릭된 상태로 설정
                chip.isClickable = false  // 클릭 불가능
                //chip.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_50))
                chip.setChipBackgroundColorResource(R.color.blue_50)
                chip.setChipStrokeColorResource(R.color.blue_300)
                binding.allergyChipGroup.addView(chip)

                chip.post {
                    Log.d("ChipWidth", "Chip ${chip.text} width: ${chip.width} px")
                }
            }
        }
    }

}