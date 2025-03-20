package com.dna.beyoureyes.ui.foodAnalysis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.data.api.SpringApiResponseHandler
import com.dna.beyoureyes.data.api.model.ApiStatus
import com.dna.beyoureyes.data.api.request.FoodRecordRequest
import com.dna.beyoureyes.databinding.FragmentResultEatBinding
import com.dna.beyoureyes.data.model.Food
import com.dna.beyoureyes.data.model.Food.Companion.toNutritionInfo
import com.dna.beyoureyes.data.model.FoodHistory
import com.dna.beyoureyes.di.SpringClient
import com.dna.beyoureyes.ui.common.CustomDialog
import com.dna.beyoureyes.ui.common.CustomToolbar
import com.dna.beyoureyes.ui.myInfo.MyInfoViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ResultEatFragment : Fragment() {
    private var _binding: FragmentResultEatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()
    private val myInfoViewModel: MyInfoViewModel by activityViewModels()
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
                // 식품 기록 데이터 중 사진 데이터 전송
                val capturedImgUri: Uri = viewModel.getCapturedImageUri()!!
                val foodData: Food = viewModel.getFoodData()!!
                foodData.scaleQuantityByFactor(scale.toDouble()) // 입력한 양 조절 반영

                lifecycleScope.launch {
                    val imgRefPath: String? = uploadImageToFirebase(capturedImgUri)
                    val timestamp = LocalDateTime.now()
                    val status =
                        imgRefPath?.let{
                            recordFoodDataToServer(timestamp, foodData, imgRefPath)
                        }?:run{ // 이미지 저장에 실패
                            Log.e("RESULT_EAT", "Failed to Upload Image To Firebase")
                            ApiStatus.SERVER_ERROR
                        }
                    when(status){
                        ApiStatus.SUCCESS -> {
                            val newHistory = FoodHistory(imgRefPath!!, timestamp, foodData.kcal!!, foodData.nutritions!!)
                            myInfoViewModel.addFoodHistory(newHistory)
                            CustomDialog(
                                msg = "섭취량 입력이 완료되었습니다.",
                                buttonCallback = { requireActivity().finish() }
                            ).show(childFragmentManager, "Dialog")

                        } ApiStatus.NETWORK_ERROR, ApiStatus.SERVER_ERROR -> {

                            CustomDialog("서버와의 연결에 실패했습니다.\n" +
                                    "다시 시도해 봐도 오류가 반복되면\n" +
                                    "앱을 다시 시작해 주세요.")
                                .show(childFragmentManager, "Dialog")

                        } ApiStatus.UNKNOWN -> {

                            CustomDialog("알 수 없는 오류가 발생했습니다.\n" +
                                    "다시 시도해 봐도 오류가 반복되면\n" +
                                    "앱을 다시 시작해 주세요.")
                                .show(childFragmentManager, "Dialog")

                        } else -> { }
                    }
                }
            }
        }
        return root
    }

    private suspend fun uploadImageToFirebase(imageUri:Uri): String? = suspendCoroutine { continuation ->
        val resizedImage = resizeImageByWidth(imageUri, 500) // 가로 500px로 조정
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference

        // 업로드할 파일의 경로 설정 (예: "images/UserId_20241215_191155.jpg")
        val imageRef: StorageReference =
            storageReference.child("foods/${AppUser.id}_${imageUri.lastPathSegment}")

        val uploadTask = imageRef.putFile(resizedImage) // Firebase Storage에 파일 업로드

        uploadTask.addOnSuccessListener {
            val imgRefPath = imageRef.path
            continuation.resume(imgRefPath)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            continuation.resume(null) // 실패 시 null 반환
        }
    }

    private suspend fun recordFoodDataToServer(timestamp:LocalDateTime, foodData: Food, imgRefPath: String): ApiStatus {
        return try {
            val request = FoodRecordRequest(
                image = imgRefPath,
                timestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                foodData = foodData.toNutritionInfo()
            )
            return suspendCancellableCoroutine { continuation ->
                lifecycleScope.launch {
                    SpringApiResponseHandler {
                        SpringClient.authSpringApi.recordFood(request)
                    }.onSuccess { _, status ->
                        continuation.resume(status) // 성공 시 true 반환
                    }.onError { status ->
                        continuation.resume(status) // 실패 시 false 반환
                    }.execute()
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.e("Failed to Record Food History", "$e")
            ApiStatus.UNKNOWN
        }
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