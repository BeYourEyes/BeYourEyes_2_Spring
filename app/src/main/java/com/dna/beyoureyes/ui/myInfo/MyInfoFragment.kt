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
import com.bumptech.glide.Glide
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentMyInfoBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.google.android.material.chip.Chip
import com.dna.beyoureyes.BuildConfig
import com.dna.beyoureyes.model.UserInfoEditMode
import com.dna.beyoureyes.ui.IconChip

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

        // 식사 기록 리사이클러 뷰 어댑터 정의 - 데이터 전달 & 아이템 클릭 시 액션 정의(프래그먼트 교체)
        foodHistoryAdapter = FoodHistoryAdapter(activity.foodHistoryItems) {
                foodHistory ->
            val action = MyInfoFragmentDirections.actionMyInfoToPastDetail(foodHistory)
            findNavController().navigate(action)
        }

        // 정의한 어댑터와 레이아웃 매니저를 식사 기록 리사이클러 뷰에 세팅
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false)
            adapter = foodHistoryAdapter
        }

        // 식사 기록 개수 세팅
        binding.historyCnt.text = "${foodHistoryAdapter.itemCount}"

        // 프로필 수정 버튼
        binding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditUserInfoActivity::class.java)
            intent.putExtra("EDIT_MODE", UserInfoEditMode.PROFILE)
            startActivity(intent)
        }

        // 질환 수정 버튼
        binding.editDisease.setOnClickListener {
            val intent = Intent(requireContext(), EditUserInfoActivity::class.java)
            intent.putExtra("EDIT_MODE", UserInfoEditMode.DISEASE)
            startActivity(intent)
        }

        // 알레르기 수정 버튼
        binding.editAllergy.setOnClickListener {
            val intent = Intent(requireContext(), EditUserInfoActivity::class.java)
            intent.putExtra("EDIT_MODE", UserInfoEditMode.ALLERGY)
            startActivity(intent)
        }

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
        binding.appVersionText.text = "version ${BuildConfig.VERSION_NAME}"
        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }

        updateProfile()

        // 스크린 리더용 contentDescription 설정
        binding.profileImgView.contentDescription = buildString { // 프로필 사진 표시
            val hasProfileImg = AppUser.info?.profileImgUri != null
            append(binding.profileImgView.contentDescription) // 내 프로필 사진
            append(". 설정된 이미지: ${if (hasProfileImg) "있음" else "없음"}")
        }
        binding.diseaseLayout.contentDescription = buildString { // 내 질환 정보
            append(binding.diseaseLabel.text) // 내 질환
            append(": ")
            append(AppUser.info?.disease?.joinToString(", ") { it.displayName } ?: "없음")
        }

        binding.allergyLayout.contentDescription = buildString { // 내 알레르기 정보
            append(binding.allergyLabel.text) // 내가 가진 알레르기 정보
            append(": ")
            append(AppUser.info?.allergens?.joinToString(", ") { it.displayName } ?: "없음")
        }
        binding.historyLabelLayout.contentDescription = buildString { // 영양 기록 제목
            append(binding.historyLabel.text)
            append(", ${foodHistoryAdapter.itemCount}개")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateProfile() {

        // 이름 설정
        binding.profileName.text = AppUser.info?.name?:""

        // 프로필 설정
        Glide.with(this)
            .load(AppUser.info?.profileImgUri)
            .centerCrop() // 이미지를 크롭
            .placeholder(R.drawable.home_good)
            .into(binding.profileImgView)

        // 질환 칩 설정
        AppUser.info?.disease?.forEach { disease ->
            // 칩 스타일 및 표시 관련 설정
            val diseaseChip = IconChip(requireContext()) // 커스텀 아이콘 칩
            diseaseChip.setNotCheckableDiseaseChip(disease) // 수정 불가능 질환 칩 세팅
            binding.diseaseChipGroup.addView(diseaseChip) // 칩 그룹에 추가
        }

        // 알레르기 칩 설정
        AppUser.info?.allergens?.forEach { alg ->
            // 칩 텍스트 및 사용자 조작 관련 설정
            val chip = Chip(context)
            chip.text =
                if (alg.displayName.length == 1) " ${alg.displayName} " // 한 글자면 width 설정 불가
                else alg.displayName
            // chip.isChecked = true  // 클릭된 상태로 설정
            chip.isClickable = false  // 클릭 불가능
            chip.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO

            // 표시 관련 설정
            chip.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.chip_width), // 85dp
                resources.getDimensionPixelSize(R.dimen.chip_height) // 40dp
            ).also { params -> params.gravity = Gravity.CENTER }
            chip.setTextAppearanceResource(R.style.chipTextMyInfo)
            chip.setChipBackgroundColorResource(R.color.blue_50)
            chip.setChipStrokeColorResource(R.color.blue_300)

            binding.allergyChipGroup.addView(chip) // 칩 추가
        }
    }

}