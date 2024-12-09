package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.NutriIntakeBarDisplay
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

        // 영양소 바 그래프들 바인딩
        val natriumBar = NutriIntakeBarDisplay(
            binding.naBarchart, binding.naName, binding.naQuant, binding.naDV)
        val carbsBar = NutriIntakeBarDisplay(
            binding.carbBarchart, binding.carbName, binding.carbQuant, binding.carbDV)
        val sugarBar = NutriIntakeBarDisplay(
            binding.sugarBarchart, binding.sugarName, binding.sugarQuant, binding.sugarDV)
        val fatBar = NutriIntakeBarDisplay(
            binding.fatBarchart, binding.fatName, binding.fatQuant, binding.fatDV)
        val satFatBar = NutriIntakeBarDisplay(
            binding.satFatBarchart, binding.satFatName, binding.satFatQuant, binding.satFatDV)
        val cholBar = NutriIntakeBarDisplay(
            binding.choleBarchart, binding.choleName, binding.choleQuant, binding.choleDV)
        val proteinBar = NutriIntakeBarDisplay(
            binding.proteinBarchart, binding.proteinName, binding.proteinQuant, binding.proteinDV)

        viewModel.foodData.observe(viewLifecycleOwner) { food ->
            food.nutritions?.forEach { nutri ->
                when(nutri) {
                    is Natrium -> natriumBar.set(requireContext(), nutri)
                    is Carbs -> carbsBar.set(requireContext(), nutri)
                    is Sugar -> sugarBar.set(requireContext(), nutri)
                    is Fat -> fatBar.set(requireContext(), nutri)
                    is SaturatedFat -> satFatBar.set(requireContext(), nutri)
                    is Cholesterol -> cholBar.set(requireContext(), nutri)
                    is Protein -> proteinBar.set(requireContext(), nutri)
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

        // 영양소 바 그래프들 바인딩
        val natriumBar = NutriIntakeBarDisplay(
            binding.naBarchart, binding.naName, binding.naQuant, binding.naDV)
        val carbsBar = NutriIntakeBarDisplay(
            binding.carbBarchart, binding.carbName, binding.carbQuant, binding.carbDV)
        val sugarBar = NutriIntakeBarDisplay(
            binding.sugarBarchart, binding.sugarName, binding.sugarQuant, binding.sugarDV)
        val fatBar = NutriIntakeBarDisplay(
            binding.fatBarchart, binding.fatName, binding.fatQuant, binding.fatDV)
        val satFatBar = NutriIntakeBarDisplay(
            binding.satFatBarchart, binding.satFatName, binding.satFatQuant, binding.satFatDV)
        val cholBar = NutriIntakeBarDisplay(
            binding.choleBarchart, binding.choleName, binding.choleQuant, binding.choleDV)
        val proteinBar = NutriIntakeBarDisplay(
            binding.proteinBarchart, binding.proteinName, binding.proteinQuant, binding.proteinDV)

        nutritionList?.let {
            Log.d("NUTRI BAR", "nutritionList exists.")
            for (nutri in it){
                when(nutri) {
                    is Natrium -> natriumBar.set(requireContext(), nutri)
                    is Carbs -> carbsBar.set(requireContext(), nutri)
                    is Sugar -> sugarBar.set(requireContext(), nutri)
                    is Fat -> fatBar.set(requireContext(), nutri)
                    is SaturatedFat -> satFatBar.set(requireContext(), nutri)
                    is Cholesterol -> cholBar.set(requireContext(), nutri)
                    is Protein -> proteinBar.set(requireContext(), nutri)
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