package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.databinding.FragmentResultNutriBarChartBinding
import com.dna.beyoureyes.model.Carbs
import com.dna.beyoureyes.model.Cholesterol
import com.dna.beyoureyes.model.Fat
import com.dna.beyoureyes.model.Natrium
import com.dna.beyoureyes.model.NutritionWrapper
import com.dna.beyoureyes.model.Protein
import com.dna.beyoureyes.model.SaturatedFat
import com.dna.beyoureyes.model.Sugar
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel

class ResultNutriBarFragment : Fragment() {

    private var _binding : FragmentResultNutriBarChartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultNutriBarChartBinding.inflate(inflater, container, false)

        // 영양소 바 그래프 갱신
        viewModel.foodData.observe(viewLifecycleOwner) { food ->
            food.nutritions?.forEach { nutrient ->
                when(nutrient) {
                    is Natrium -> binding.natriumBar.setData(nutrient)
                    is Carbs -> binding.carbsBar.setData(nutrient)
                    is Sugar -> binding.sugarBar.setData(nutrient)
                    is Fat -> binding.fatBar.setData(nutrient)
                    is SaturatedFat -> binding.satFatBar.setData(nutrient)
                    is Cholesterol -> binding.cholesterolBar.setData(nutrient)
                    is Protein -> binding.proteinBar.setData(nutrient)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 외부에서 Bundle로 전달받은 영양소 데이터
        val nutritionWrapperList : List<NutritionWrapper>? =
            arguments?.getParcelableArrayList("nutritionList")
        val nutritionList = nutritionWrapperList?.map { it.nutrition }

        // 영양소 바 그래프 갱신
        nutritionList?.let {
            Log.d("NUTRI BAR", "nutritionList exists.")
            for (nutrient in it){
                when(nutrient) {
                    is Natrium -> binding.natriumBar.setData(nutrient)
                    is Carbs -> binding.carbsBar.setData(nutrient)
                    is Sugar -> binding.sugarBar.setData(nutrient)
                    is Fat -> binding.fatBar.setData(nutrient)
                    is SaturatedFat -> binding.satFatBar.setData(nutrient)
                    is Cholesterol -> binding.cholesterolBar.setData(nutrient)
                    is Protein -> binding.proteinBar.setData(nutrient)
                }
            }
        } ?: run {
            Log.d("NUTRI BAR", "nutritionList doesn't exist.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}