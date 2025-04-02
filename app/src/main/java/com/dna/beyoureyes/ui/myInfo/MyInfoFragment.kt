package com.dna.beyoureyes.ui.myInfo

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentMyInfoBinding
import com.dna.beyoureyes.ui.common.CustomToolbar
import com.google.android.material.chip.Chip
import com.dna.beyoureyes.BuildConfig
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.data.api.SpringApiResponseHandler
import com.dna.beyoureyes.data.api.model.ApiStatus
import com.dna.beyoureyes.data.model.Allergen
import com.dna.beyoureyes.data.model.Disease
import com.dna.beyoureyes.di.SpringClient
import com.dna.beyoureyes.ui.assign.AssignMode
import com.dna.beyoureyes.ui.common.IconChip
import com.dna.beyoureyes.ui.assign.AssignActivity
import com.dna.beyoureyes.ui.common.CustomDialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class MyInfoFragment : Fragment() {
    private var _binding: FragmentMyInfoBinding? = null
    private val binding get() = _binding!!

    private val myInfoViewModel: MyInfoViewModel by lazy {
        (requireActivity() as MainActivity).myInfoViewModel
    }

    private val playStoreBaseLink = "https://play.google.com/store/apps/details?id="
    private var lastAssignMode: AssignMode? = null // 마지막으로 실행한 AssignMode 저장
    private lateinit var foodHistoryAdapter: FoodHistoryAdapter

    private val assignActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) { // Assign Activity를 통한 유저 데이터 수정을 마쳤다면
            when (lastAssignMode) {
                AssignMode.PROFILE -> {
                    // 프로필 UI 업데이트
                    updateProfileUI(AppUser.info?.name)
                    Log.d("ASSIGN_SUCCESS", "프로필이 업데이트되었습니다!")
                }
                AssignMode.DISEASE -> {
                    // 질환 UI 업데이트
                    updateDiseaseUI(AppUser.info?.disease)
                    Log.d("ASSIGN_SUCCESS", "질환 정보가 업데이트되었습니다!")
                }
                AssignMode.ALLERGY -> {
                    // 알레르기 UI 업데이트
                    updateAllergenUI(AppUser.info?.allergens)
                    Log.d("ASSIGN_SUCCESS", "알레르기 정보가 업데이트되었습니다!")
                }
                else -> {}
            }
            lastAssignMode = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 식사 기록 리사이클러 뷰 어댑터 정의 - 데이터 전달 & 아이템 클릭 시 액션 정의(프래그먼트 교체)
        foodHistoryAdapter = FoodHistoryAdapter(mutableListOf()) { foodHistory ->
            myInfoViewModel.selectFoodHistory(foodHistory) // 선택한 음식 기록을 ViewModel에 저장
            findNavController().navigate(R.id.navigation_past_detail)
        }
        // 정의한 어댑터와 레이아웃 매니저를 식사 기록 리사이클러 뷰에 세팅
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = foodHistoryAdapter
        }
        // 프로필 UI 업데이트
        updateProfileUI(AppUser.info?.name)
        // 질환 UI 업데이트
        updateDiseaseUI(AppUser.info?.disease)
        // 알레르기 UI 업데이트
        updateAllergenUI(AppUser.info?.allergens)

        // 프로필 이미지 UI 설정
        Glide.with(this)
            .load(AppUser.info?.profileImgUri)
            .centerCrop() // 이미지를 크롭
            .placeholder(R.drawable.home_good)
            .into(binding.profileImgView)
        // 프로필 이미지 contentDescription
        binding.profileImgView.contentDescription = buildString { // 프로필 사진 표시
            val hasProfileImg = (AppUser.info?.profileImgUri != null)
            append(binding.profileImgView.contentDescription) // 내 프로필 사진
            append(". 설정된 이미지: ${if (hasProfileImg) "있음" else "없음"}")
        }

        binding.appVersionText.text = "version ${BuildConfig.VERSION_NAME}" // 버전 이름 UI 설정

        // Live Data 옵저빙 및 UI 업데이트
        myInfoViewModel.foodHistoryItems.observe(viewLifecycleOwner) { foodHistories ->
            foodHistoryAdapter.updateList(foodHistories)
            binding.historyCnt.text = "${foodHistories.size}" // 식사 기록 개수 세팅
            updateHistoryUIdescription(foodHistories.size)
        }

        // 프로필 수정 버튼
        binding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), AssignActivity::class.java)
            intent.putExtra("EDIT_MODE", AssignMode.PROFILE)
            lastAssignMode = AssignMode.PROFILE
            assignActivityLauncher.launch(intent) // AssignActivity 실행 후 결과 감지
        }
        // 질환 수정 버튼
        binding.editDisease.setOnClickListener {
            val intent = Intent(requireContext(), AssignActivity::class.java)
            intent.putExtra("EDIT_MODE", AssignMode.DISEASE)
            lastAssignMode = AssignMode.DISEASE
            assignActivityLauncher.launch(intent) // AssignActivity 실행 후 결과 감지
        }
        // 알레르기 수정 버튼
        binding.editAllergy.setOnClickListener {
            val intent = Intent(requireContext(), AssignActivity::class.java)
            intent.putExtra("EDIT_MODE", AssignMode.ALLERGY)
            lastAssignMode = AssignMode.ALLERGY
            assignActivityLauncher.launch(intent) // AssignActivity 실행 후 결과 감지
        }

        // 앱 공유하기 버튼
        binding.shareBtn.setOnClickListener {
            val packageName: String = requireContext().packageName // 앱의 패키지 이름 가져오기
            val content = "더 건강한 식습관으로 당신의 삶을 변화시키세요!\n" + "지금 바로 앱을 다운로드하세요 ⬇️"
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "$content\n\n$playStoreBaseLink$packageName")
            startActivity(Intent.createChooser(intent, "친구에게 공유하기"))

        }
        // 앱 스토어 리뷰 남기기
        binding.reviewBtn.setOnClickListener {
            openPlayStoreReviewPage()
        }
        // 고객센터 문의하기
        binding.contactBtn.setOnClickListener {
            val contactLink = "http://pf.kakao.com/_Bdqun/chat"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contactLink))
            startActivity(intent)
        }
        // 개인정보 처리 방침
        binding.privacyPolicyBtn.setOnClickListener {
            val policyLink = "https://polar-wolf-52e.notion.site/3ff38ff000ef47b99fa8d4e890a6b4a7?pvs=74"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyLink))
            startActivity(intent)
        }
        // 탈퇴하기
        binding.signOutBtn.setOnClickListener {
            val userName = AppUser.info?.name ?: "사용자"
            CustomDialog(
                msg = "정말로 탈퇴하시겠습니까?\n${userName}님의 정보와 기록이\n영구적으로 삭제될 거에요.",
                buttonText = "그래도 탈퇴하기",
                buttonCallback = { signOutWithExceptionHandling() }, // 예외 처리가 포함된 탈퇴 로직
                secondaryButtonText = "취소하기"
            ).show(childFragmentManager, "Dialog")
        }

        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOutWithExceptionHandling() {
        lifecycleScope.launch {
            val status = signOut()
            when(status) {
                ApiStatus.SUCCESS -> { // 탈퇴 성공
                    AppUser.signOut()
                    CustomDialog(
                        msg = "탈퇴 처리를 마쳤습니다.\n다음에 또 이용해주세요!",
                        buttonCallback = { requireActivity().finishAffinity() }
                    ).show(childFragmentManager, "Dialog")

                } ApiStatus.SERVER_ERROR -> {
                    CustomDialog("서버 응답이 원활하지 못해\n탈퇴 처리에 실패했습니다.\n다시 시도해주세요!")
                        .show(childFragmentManager, "Dialog")

                } ApiStatus.NETWORK_ERROR -> {
                    CustomDialog("요청에 실패했습니다.\n네트워크 연결 상태를 확인 후\n다시 시도해주세요!")
                        .show(childFragmentManager, "Dialog")

                } ApiStatus.UNKNOWN -> {
                    CustomDialog("알 수 없는 오류로 인해\n요청에 실패했습니다.\n다시 시도해주세요!")
                        .show(childFragmentManager, "Dialog")

                } else -> { }
            }
        }
    }

    private suspend fun signOut(): ApiStatus = suspendCancellableCoroutine { continuation ->
        lifecycleScope.launch {
            SpringApiResponseHandler {
                SpringClient.authSpringApi.deleteUser()
            }.onSuccess{ _, status ->
                continuation.resume(status)
            }.onError { status ->
                continuation.resume(status)
            }.execute()
        }
    }

    private fun updateHistoryUIdescription(historyCount: Int) {
        binding.historyLabelLayout.contentDescription = buildString { // 영양 기록 제목
            append(binding.historyLabel.text)
            append(", ${historyCount}개")
        }
    }

    private fun updateProfileUI(name: String?) { // UI 업데이트
        name?.let { binding.profileName.text = it } // 이름 설정
    }

    private fun updateDiseaseUI(disease: Set<Disease>?) {
        // UI 업데이트
        binding.diseaseChipGroup.removeAllViews()
        disease?.forEach { // 질환 칩 설정
            // 칩 스타일 및 표시 관련 설정
            val diseaseChip = IconChip(requireContext()) // 커스텀 아이콘 칩
            diseaseChip.setNotCheckableDiseaseChip(it) // 수정 불가능 질환 칩 세팅
            binding.diseaseChipGroup.addView(diseaseChip) // 칩 그룹에 추가
        }
        // 스크린 리더용 contentDescription 업데이트
        binding.diseaseLayout.contentDescription = buildString { // 내 질환 정보
            append(binding.diseaseLabel.text) // 내 질환
            append(": ")
            append(disease?.joinToString(", ") { it.displayName } ?: "없음")
        }
    }

    private fun updateAllergenUI(allergens: Set<Allergen>?) {
        // UI 업데이트
        binding.allergyChipGroup.removeAllViews()
        allergens?.forEach { alg -> // 알레르기 칩 설정
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
        // 스크린 리더용 contentDescription 업데이트
        binding.allergyLayout.contentDescription = buildString { // 내 알레르기 정보
            append(binding.allergyLabel.text) // 내가 가진 알레르기 정보
            append(": ")
            append(allergens?.joinToString(", ") { it.displayName } ?: "없음")
        }
    }

    private fun openPlayStoreReviewPage() {
        val packageName: String = requireContext().packageName // 앱의 패키지 이름 가져오기
        try {
            // Google Play Store 앱의 리뷰 페이지 URL 생성 (market:// 방식)
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName"))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Google Play Store 앱이 설치되어 있지 않은 경우, 웹 브라우저로 연결 (https:// 방식)
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse(playStoreBaseLink+packageName))
            startActivity(intent)
        }
    }

}