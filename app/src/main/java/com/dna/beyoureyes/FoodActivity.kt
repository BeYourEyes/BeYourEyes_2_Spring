package com.dna.beyoureyes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.dna.beyoureyes.databinding.ActivityFoodBinding
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel
import com.dna.beyoureyes.util.FoodTextRecognizer
import com.dna.beyoureyes.util.ImageProcessor
import kotlinx.coroutines.launch

class FoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodBinding
    private lateinit var navController: NavController

    private val viewModel: FoodViewModel by viewModels()
    private val imageProcessor: ImageProcessor = ImageProcessor(this) // 구 Camera 객체

    // 카메라 권한 요청 결과 처리 - Activity Result API 활용 (간결성, 타입 안전성, 수명 주기 관리 장점)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용되면 카메라 실행
                imageProcessor.captureImage()
            } else {
                // 권한이 거부되면 토스트 메시지 표시
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 바인딩
        binding = ActivityFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment_activity_food)

    }

    fun startCamera() {
        if (checkCameraPermission()) { // 카메라 권한 확인
            imageProcessor.captureImage()
        } else {
            requestCameraPermission() // 카메라 권한 요청
        }
    }

    // 촬영 후 이미지 검사 및 후처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageProcessor.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageProcessor.getImageUri()?.let { capturedImageUri ->
                if (imageProcessor.isValidImage(capturedImageUri)) {
                    // 유효성 검사 통과
                    // 촬영한 사진 회전 방향 조정 및 저장
                    val srcUri = imageProcessor.saveCapturedImageToInternalStorage(capturedImageUri)
                    srcUri?.let { srcUri ->

                        viewModel.setCapturedImageUri(srcUri) // 회전 방향 수정한 이미지를 뷰모델에 저장

                        // OCR 입력용 이미지 처리
                        imageProcessor.processImageAndSaveToInternalStorage(srcUri)?.let{ procUri ->
                            viewModel.setProcessedImageUri(procUri) // ocr 입력용 처리한 이미지를 저장
                        }

                        // 로딩 화면 시작
                        navController.navigate(R.id.food_navi_loading)

                    }?: run {
                        // 사진 저장 오류 -> 아마 발생 X
                        navController.navigate(R.id.food_navi_fail)
                    }
                } else {
                    // 유효성 검사 실패, 재촬영 안내 프래그먼트 표시
                    navController.navigate(R.id.food_navi_fail)
                }
            }
        }
    }

    // 카메라 권한 확인
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 요청
    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        imageProcessor.deleteImagesFromInternalStorage() // 내부 저장소 임시 이미지 파일 정리
    }

    private fun testCamera() {


    }}