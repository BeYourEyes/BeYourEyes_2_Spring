package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.ui.common.EnergyChart
import com.dna.beyoureyes.databinding.FragmentResultNutriPieChartBinding
import com.dna.beyoureyes.data.model.CaloricNutrient
import com.dna.beyoureyes.data.model.Carbs
import com.dna.beyoureyes.data.model.Fat
import com.dna.beyoureyes.data.model.NutritionWrapper
import com.dna.beyoureyes.data.model.Protein
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel

class ResultNutriPieFragment : Fragment() {

    private var _binding : FragmentResultNutriPieChartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultNutriPieChartBinding.inflate(inflater, container, false)

        val pieChart = EnergyChart(binding.resultNutriPiehart)
        viewModel.foodData.observe(viewLifecycleOwner) { food ->
            val caloricNutrients = food.nutritions?.filterIsInstance<CaloricNutrient>()
            caloricNutrients?.forEach { nutri ->
                when(nutri) {
                    is Carbs -> pieChart.setKcalOfCarbs(nutri.kcal.toFloat())
                    is Fat -> pieChart.setKcalOfFat(nutri.kcal.toFloat())
                    is Protein -> pieChart.setKcalOfProtein(nutri.kcal.toFloat())
                }
                updateContentDescriptions(caloricNutrients)
            }
            pieChart.setChart(requireContext())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 외부에서 Bundle로 전달받은 영양소 데이터
        val nutritionWrapperList : List<NutritionWrapper>? =
            arguments?.getParcelableArrayList("nutritionList")
        val nutritionList = nutritionWrapperList?.map { it.nutrition }

        val pieChart = EnergyChart(binding.resultNutriPiehart)

        nutritionList?.let { nutris ->
            Log.d("Nutri : ", "nutritionList exists.")
            val caloricNutrients = nutris.filterIsInstance<CaloricNutrient>()
            for (nutri in caloricNutrients){
                when(nutri) {
                    is Carbs -> pieChart.setKcalOfCarbs(nutri.kcal.toFloat())
                    is Fat -> pieChart.setKcalOfFat(nutri.kcal.toFloat())
                    is Protein -> pieChart.setKcalOfProtein(nutri.kcal.toFloat())
                }
            }
            pieChart.setChart(requireContext())
            updateContentDescriptions(caloricNutrients)
        } ?: run {
            Log.d("Nutri : ", "nutritionList doesn't exist.")
        }
        //pieChart.setChart(requireContext())

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateContentDescriptions(caloricNutrients: List<CaloricNutrient>) {
        val total = caloricNutrients.sumOf { it.kcal }
        val percentTexts = caloricNutrients.joinToString(", ") {
            "${it.name} ${(it.kcal.toDouble() / total.toDouble() * 100).toInt()}%"
        }
        binding.parentLayout.contentDescription = buildString {
            append("${binding.pieChartTitle.text}. ")
            append("이 식품의 에너지 성분 구성은 $percentTexts 입니다.")
            append("이 정보는 대략적인 값으로 참고용입니다.")
        }
    }


}