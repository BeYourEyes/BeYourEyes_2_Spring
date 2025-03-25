package com.dna.beyoureyes.ui.myInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.ui.foodDetail.ResultKcalFragment
import com.dna.beyoureyes.ui.foodDetail.ResultNutriBarFragment
import com.dna.beyoureyes.databinding.FragmentMyPastDetailBinding
import com.dna.beyoureyes.data.model.NutritionWrapper
import com.dna.beyoureyes.ui.common.CustomToolbar
import org.threeten.bp.format.DateTimeFormatter


class MyPastDetailFragment : Fragment() {
    private var _binding: FragmentMyPastDetailBinding? = null
    private val binding get() = _binding!!

    private val myInfoViewModel: MyInfoViewModel by lazy {
        (requireActivity() as MainActivity).myInfoViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyPastDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 과거 기록 데이터 UI 세팅
        myInfoViewModel.selectedFoodHistory.value?.let{ history ->
            // 식품 사진
            Glide.with(this)
                .load(history.imgUrl)
                .centerCrop() // 이미지를 크롭
                .into(binding.imageView)

            // 기록 날짜
            val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)") // 날짜 형식
            val timeFormat = DateTimeFormatter.ofPattern("HH:mm a") // 시간 형식
            binding.dateTextView.text = dateFormat.format(history.timestamp)
            binding.timeTextView.text = timeFormat.format(history.timestamp)

            // 칼로리 정보
            binding.kcalFragment.getFragment<ResultKcalFragment>().arguments = Bundle().apply {
                putInt("kcal", history.kcal)
            }

            // 영양성분 함유량 정보를 영양소바 프래그먼트에 Bundle로 전달
            binding.nutriBarFragment.getFragment<ResultNutriBarFragment>().arguments = Bundle().apply {
                putParcelableArrayList(
                    "nutritionList", ArrayList(history.nutritions.map{ NutritionWrapper(it) })
                )
            }
        }

        binding.toolbar.closeButtonClickListener= object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        // 스크린 리더 대응용 contentDescription 설정
        binding.dateTimeLayout.contentDescription =
            "${binding.dateTextView.text}, ${binding.timeTextView.text} 기록."

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        myInfoViewModel.clearSelectedFoodHistory()
    }

}