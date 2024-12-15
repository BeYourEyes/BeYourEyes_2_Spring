package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.model.EnergyChart
import com.dna.beyoureyes.databinding.FragmentResultNutriPieChartBinding
import com.dna.beyoureyes.model.Carbs
import com.dna.beyoureyes.model.Fat
import com.dna.beyoureyes.model.NutritionWrapper
import com.dna.beyoureyes.model.Protein
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
            food.nutritions?.forEach { nutri ->
                when(nutri) {
                    is Carbs -> pieChart.setKcalOfCarbs(nutri.kcal.toFloat())
                    is Fat -> pieChart.setKcalOfFat(nutri.kcal.toFloat())
                    is Protein -> pieChart.setKcalOfProtein(nutri.kcal.toFloat())
                }
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

        nutritionList?.let {
            Log.d("Nutri : ", "nutritionList exists.")
            for (nutri in it){
                when(nutri) {
                    is Carbs -> pieChart.setKcalOfCarbs(nutri.kcal.toFloat())
                    is Fat -> pieChart.setKcalOfFat(nutri.kcal.toFloat())
                    is Protein -> pieChart.setKcalOfProtein(nutri.kcal.toFloat())
                }
            }
            pieChart.setChart(requireContext())
        } ?: run {
            Log.d("Nutri : ", "nutritionList doesn't exist.")
        }
        //pieChart.setChart(requireContext())

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}