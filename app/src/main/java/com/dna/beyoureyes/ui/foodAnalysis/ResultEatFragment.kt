package com.dna.beyoureyes.ui.foodAnalysis

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.databinding.FragmentResultEatBinding
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.model.Food
import com.dna.beyoureyes.ui.CustomToolbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.sql.Timestamp
import java.util.Calendar

class ResultEatFragment : Fragment() {
    private var _binding: FragmentResultEatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 레이아웃 바인딩
        _binding = FragmentResultEatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }

        // ViewModel에서 사진 Uri를 가져와 ImageView에 표시
        viewModel.capturedImageUri.observe(viewLifecycleOwner) { uri ->
            binding.ImageCaptured.setImageURI(uri)
        }

        binding.resultButtonEat.setOnClickListener {
            // 식품 기록 데이터 전송 후 종료해야 함. 전송 로직 작성 필요.

            // 식품 기록 데이터 중 사진 데이터 전송
            var imgUrl = ""
            val imageUri: Uri = viewModel.getCapturedImageUri()!!
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference

            // 업로드할 파일의 경로 설정 (예: "images/IMG_20241215_191155.jpg")
            val imageRef: StorageReference = storageReference.child("foods/${imageUri.lastPathSegment}")
            // Firebase Storage에 파일 업로드
            val uploadTask = imageRef.putFile(imageUri)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // 다운로드 가능한 URL을 받아오는 부분
                    imgUrl = downloadUri.toString()
                }
            }.addOnFailureListener { exception ->
                // 업로드 실패 시
                exception.printStackTrace()
            }

            // viewModel에 Food 형식의 데이터로 필요한 데이터 저장.
            if (viewModel.isFoodDataAvaiable()) {
                val eatFoodData : Food = viewModel.getFoodData()!!
                val hashFoodData : HashMap<String, Any?> = hashMapOf(
                    "calories" to eatFoodData.kcal,
                    "protein" to eatFoodData.nutritions?.find { it.name == "단백질" }?.milligram,
                    "carbs" to eatFoodData.nutritions?.find { it.name == "탄수화물" }?.milligram,
                    "chol" to eatFoodData.nutritions?.find { it.name == "콜레스테롤" }?.milligram,
                    "fat" to eatFoodData.nutritions?.find { it.name == "지방" }?.milligram,
                    "natrium" to eatFoodData.nutritions?.find { it.name == "나트륨" }?.milligram,
                    "satFat" to eatFoodData.nutritions?.find { it.name == "포화지방" }?.milligram,
                    "sugar" to eatFoodData.nutritions?.find { it.name == "당류" }?.milligram,
                    "date" to com.google.firebase.Timestamp.now(),
                    "userId" to AppUser.id,
                    "imgPath" to "foods/${imageUri.lastPathSegment}"
                )
                FirebaseHelper.sendData(hashFoodData, "userIntakeNutrition")

            }
            // viewModel에 적절한 get 함수를 짜는 등...으로 칼로리, 영양정보 데이터 가져오기
            // 참고로 Food 객체에 양 조절 메소드 있음. (Food 변수).scaleQuantityByFactor(0.5)처럼 사용
            requireActivity().finish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}