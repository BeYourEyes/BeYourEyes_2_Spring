package com.dna.beyoureyes.ui.foodAnalysis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentResultEatBinding
import com.dna.beyoureyes.databinding.ResultEatDialogBinding
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.model.Food
import com.dna.beyoureyes.ui.CustomToolbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp
import java.util.Calendar

class ResultEatFragment : Fragment() {
    private var _binding: FragmentResultEatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()
    private var scale = 0f

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

        binding.eatResultRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.button25 -> {
                    scale = 0.25f
                }
                R.id.button50 -> {
                    scale = 0.5f
                }
                R.id.button75 -> {
                    scale = 0.75f
                }
                R.id.button100 -> {
                    scale = 1f
                }
            }
        }

        // ViewModel에서 사진 Uri를 가져와 ImageView에 표시
        viewModel.capturedImageUri.observe(viewLifecycleOwner) { uri ->
            binding.ImageCaptured.setImageURI(uri)
        }

        binding.resultButtonEat.setOnClickListener {
            if (scale == 0f) {
                Toast.makeText(requireContext(), "섭취하신 양을 선택해주세요!", Toast.LENGTH_SHORT).show()
            }
            else {
                // 식품 기록 데이터 전송 후 종료해야 함. 전송 로직 작성 필요.

                // 식품 기록 데이터 중 사진 데이터 전송
                var imgUrl = ""
                val imageUri: Uri = viewModel.getCapturedImageUri()!!

                val resizedImage = resizeImageByWidth(imageUri, 500) // 가로 500px로 조정

                val storageReference: StorageReference = FirebaseStorage.getInstance().reference

                // 업로드할 파일의 경로 설정 (예: "images/UserId_20241215_191155.jpg")
                val imageRef: StorageReference = storageReference.child("foods/${AppUser.id}_${imageUri.lastPathSegment}")
                // Firebase Storage에 파일 업로드
                val uploadTask = imageRef.putFile(resizedImage)

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
                // viewModel에 적절한 get 함수를 짜는 등...으로 칼로리, 영양정보 데이터 가져오기
                val isSuccess = if (viewModel.isFoodDataAvaiable()) {
                    val eatFoodData : Food = viewModel.getFoodData()!!
                    // 참고로 Food 객체에 양 조절 메소드 있음. (Food 변수).scaleQuantityByFactor(0.5)처럼 사용
                    eatFoodData.scaleQuantityByFactor(scale.toDouble())
                    val hashFoodData : HashMap<String, Any?> = hashMapOf(
                        "calories" to eatFoodData.kcal,
                        "date" to com.google.firebase.Timestamp.now(),
                        "userId" to AppUser.id,
                        "imgPath" to "foods/${AppUser.id}_${imageUri.lastPathSegment}"
                    )
                    val hashNutritionData : HashMap<String, Int>? =  // 영양소 함유량 해시맵 한번에 변환
                        eatFoodData.nutritions?.associateBy { it.dbFiledName }
                            ?.mapValues { it.value.milligram } as HashMap<String, Int>?
                    hashNutritionData?.let {
                        hashFoodData.putAll(it) // 영양소 함유량 해시맵 변환 성공하면 데이터에 추가
                    }
                    // 성공 실패 여부 반환하게 수정
                    FirebaseHelper.sendData(hashFoodData, "userIntakeNutrition")
                } else { false }
                showDialog(isSuccess)
            }
        }

        return root
    }

    private fun showDialog(isSuccess:Boolean) { // 추후 성공/실패 여부 확인 가능하도록
        // 1. AlertDialog.Builder 생성
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)

        // 2. LayoutInflater를 사용하여 레이아웃 인플레이트
        val dialogBinding = ResultEatDialogBinding.inflate(LayoutInflater.from(requireContext()))

        // 3. setView()로 사용자 정의 레이아웃 설정
        builder.setView(dialogBinding.root)

        if (!isSuccess) {
            dialogBinding.resultEatDialog.text = "오류로 인해 섭취량 입력에 실패했습니다."
        }

        // 다이얼로그 내부 버튼에 대한 리스너 설정
        dialogBinding.button.setOnClickListener {
            requireActivity().finish()
        }

        // 5. create()로 AlertDialog 생성
        val dialog = builder.create()

        // 6. show()로 다이얼로그 표시
        dialog.show()
    }

    private fun resizeImageByWidth(uri: Uri, targetWidth: Int): Uri {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // 원본 가로, 세로 크기
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height

        // 비율 계산
        val scaleFactor = targetWidth / originalWidth.toFloat()
        val targetHeight = (originalHeight * scaleFactor).toInt()
        val bitMap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

        // 크기 조정
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

        // Bitmap을 파일로 저장
        val resizedFile = File(requireContext().cacheDir, "resized_image.jpg")
        val outputStream = FileOutputStream(resizedFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        // 저장된 파일을 Uri로 변환하여 반환
        return Uri.fromFile(resizedFile)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}