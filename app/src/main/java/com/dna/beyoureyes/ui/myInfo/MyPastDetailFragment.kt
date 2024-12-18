package com.dna.beyoureyes.ui.myInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dna.beyoureyes.ui.foodDetail.ResultKcalFragment
import com.dna.beyoureyes.ui.foodDetail.ResultNutriBarFragment
import com.dna.beyoureyes.databinding.FragmentMyPastDetailBinding
import com.dna.beyoureyes.model.NutritionWrapper
import com.dna.beyoureyes.ui.CustomToolbar
import java.text.SimpleDateFormat
import java.util.Locale


class MyPastDetailFragment : Fragment() {
    private var _binding: FragmentMyPastDetailBinding? = null
    private val binding get() = _binding!!

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

        // 전달받은 과거 기록 데이터
        val foodHistory = MyPastDetailFragmentArgs.fromBundle(requireArguments()).foodHistory

        // 식품 사진
        Glide.with(this)
            .load(foodHistory.imgUri)
            .centerCrop() // 이미지를 크롭
            .into(binding.imageView)

        // 기록 날짜
        foodHistory.timestamp?.let {
            val datetime = it.toDate()
            val dateFormat = SimpleDateFormat("yyyy.MM.dd (E)", Locale.KOREA) // 날짜 형식
            val timeFormat = SimpleDateFormat("HH:mm a", Locale.US) // 시간 형식
            binding.dateTextView.text = dateFormat.format(datetime)
            binding.timeTextView.text = timeFormat.format(datetime)
        }

        // 칼로리 정보를 칼로리 프래그먼트에 Bundle로 전달
        foodHistory.kcal?.let {
            binding.kcalFragment.getFragment<ResultKcalFragment>().arguments = Bundle().apply {
                putInt("kcal", it)
            }
        }

        // 영양성분 함유량 정보를 영양소바 프래그먼트에 Bundle로 전달
        binding.nutriBarFragment.getFragment<ResultNutriBarFragment>().arguments = Bundle().apply {
            putParcelableArrayList(
                "nutritionList", ArrayList(foodHistory.nutritions.map{ NutritionWrapper(it) })
            )
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
    }

}