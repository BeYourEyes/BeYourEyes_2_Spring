package com.dna.beyoureyes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.dna.beyoureyes.databinding.FragmentAssignDiseaseBinding
import com.dna.beyoureyes.model.diseaseInfo
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener


class AssignDiseaseFragment : Fragment() {
    private lateinit var binding : FragmentAssignDiseaseBinding
    private lateinit var diseaseArray: Array<diseaseInfo>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssignDiseaseBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val listener = activity as? FragmentNavigationListener
        binding.nextBtn.setOnClickListener {
            listener?.onDiseaseInputRecieved(getClickedDiseaseList())
            listener?.onBtnClick(this, true)
        }
        binding.toolbar.backButtonClickListener = object : CustomToolbar.BackButtonClickListener {
            override fun onBackButtonClicked() {
                listener?.onBtnClick(this@AssignDiseaseFragment, false)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 초기화
        diseaseArray = arrayOf(
            diseaseInfo(
                name = "no",
                button = binding.imageView1,
                isClicked = false,
                normalImage = R.drawable.assign_disease_no,
                clickedImage = R.drawable.assign_disease_no_click
            ),
            diseaseInfo(
                name = "diabete",
                button = binding.imageView2,
                isClicked = false,
                normalImage = R.drawable.assign_disease_diabetes,
                clickedImage = R.drawable.assign_disease_diabetes_click
            ),
            diseaseInfo(
                name = "highblood",
                button = binding.imageView3,
                isClicked = false,
                normalImage = R.drawable.assign_disease_highblood,
                clickedImage = R.drawable.assign_disease_highblood_click
            ),
            diseaseInfo(
                name = "hyperlipidemia",
                button = binding.imageView4,
                isClicked = false,
                normalImage = R.drawable.assign_disease_hyperlipidemia,
                clickedImage = R.drawable.assign_disease_hyperlipidemia_click
            )
        )
        for (i in 0 until 4) {
            imageButtonClick(diseaseArray, i)
        }

    }

    fun getClickedDiseaseList() : ArrayList<String>{
        val clickedList = ArrayList<String>()
        diseaseArray.forEach {
            if (it.isClicked) {
                clickedList.add(it.name)
            }
        }
        return clickedList
    }

    fun imageButtonClick(diseaseArray: Array<diseaseInfo>, idx: Int) {
        diseaseArray[idx].button.setOnClickListener {
            val clickedDisease = diseaseArray[idx]

            // 클릭 상태 전환
            clickedDisease.isClicked = !clickedDisease.isClicked

            if (clickedDisease.isClicked) {
                // 선택된 버튼 이미지를 변경
                clickedDisease.button.setBackgroundResource(clickedDisease.clickedImage)

                if (idx == 0) { // "None" 버튼이 선택되었을 때
                    diseaseArray.forEachIndexed { i, disease ->
                        if (i != 0) { // "None" 버튼 제외
                            disease.isClicked = false
                            disease.button.setBackgroundResource(disease.normalImage)
                        }
                    }
                } else { // 다른 질병 버튼이 선택되었을 때
                    val noneDisease = diseaseArray[0]
                    noneDisease.isClicked = false
                    noneDisease.button.setBackgroundResource(noneDisease.normalImage)
                }
            } else {
                // 클릭 해제 시 기본 이미지로 변경
                clickedDisease.button.setBackgroundResource(clickedDisease.normalImage)
            }
        }
    }
}