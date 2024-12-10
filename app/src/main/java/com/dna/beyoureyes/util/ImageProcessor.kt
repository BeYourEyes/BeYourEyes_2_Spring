package com.dna.beyoureyes.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.dna.beyoureyes.BuildConfig
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageProcessor(private val context: Context) {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val BLUR_SCORE_THRESH = 500
    }

    init {
        OpenCVLoader.initDebug()
    }

    private var imageUri: Uri? = null
    // private var processedImageUri: Uri? = null

    fun getImageUri(): Uri? { return imageUri }

    // 카메라를 실행하여 사진을 촬영하고 내부 저장소에 임시 저장
    fun captureImage() {
        val file = createImageFile() // 생성할 이미지 파일 설정
        // FileProvider를 사용하여 파일의 Content URI 가져오기
        imageUri = FileProvider.getUriForFile(
            context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)

        // 카메라 인텐트 실행, 사진 촬영
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        (context as AppCompatActivity).startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    // 이미지 유효성 검사
    fun isValidImage(imageUri: Uri): Boolean {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        val blur_score = getBlurScore(bitmap)
        return (blur_score < BLUR_SCORE_THRESH)
    }

    fun saveCapturedImageToInternalStorage(imageUri: Uri): Uri? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

            // 만약 앱 내에서 CameraX나 Camera2 라이브러리로 구현하게 수정하면
            // 카메라 설정에서 회전 각도 고정시킬 수 있음. 지금처럼 따로 회전 후처리 필요 X
            val exifInterface = ExifInterface(context.contentResolver.openInputStream(imageUri)!!)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            // 이미지를 정방향으로 회전
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap // 이미 90도 회전
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                ExifInterface.ORIENTATION_NORMAL -> bitmap // 정방향
                else -> bitmap // 그 외 그대로
            }

            val file = createImageFile() // createImageFile() 함수 사용
            FileOutputStream(file).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                // 회전된 비트맵을 저장
            }
            FileProvider.getUriForFile(context,
                "${BuildConfig.APPLICATION_ID}.fileprovider", file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Bitmap을 내부 저장소에 저장하고 Uri를 반환
    fun processImageAndSaveToInternalStorage(imageUri: Uri): Uri? {
        // 촬영한 사진 저장본 읽어오기
        val srcMat = Mat()
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        Utils.bitmapToMat(bitmap, srcMat)
        val processedBitmap = histogramEqual(bitmap)
        val file = createImageFile("PROCESSED_") // prefix 추가

        FileOutputStream(file).use { out ->
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return FileProvider.getUriForFile(
            context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
    }

    // 임시로 저장한 사진 파일 모두 삭제
    fun deleteImagesFromInternalStorage() {
        // 내부 저장소 images 디렉터리의 모든 파일 삭제
        val imagesDir = File(context.filesDir, "images")
        if (imagesDir.exists()) {
            imagesDir.walkTopDown().forEach { file ->
                if (file.isFile && file.name != ".nomedia") {
                    file.delete()
                }
            }
        }
    }

    // 이미지 파일 설정 helper(저장 경로, 파일명)
    private fun createImageFile(prefix: String = ""): File {
        // 앱의 내부 저장소 디렉터리 가져와 하위에 images 디렉토리 생성
        val imagesDir = File(context.filesDir, "images")
        imagesDir.mkdirs()

        // .nomedia 파일 생성 (갤러리에서 이 경로에 있는 이미지 파일 못 읽게 하기)
        val nomediaFile = File(imagesDir, ".nomedia")
        nomediaFile.createNewFile()

        // 시간 기준으로 파일명 지정(prefix 설정 가능)
        val fileName = "${prefix}IMG_" +
                "${SimpleDateFormat("yyyyMMdd_HHmmss", 
                    Locale.getDefault()).format(Date())}.jpg"
        return File(imagesDir, fileName)
    }

    // 이미지의 흐린 정도 계산 helper (Laplacian 값)
    private fun getBlurScore(bitmap: Bitmap): Double {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val src = Mat()
        Utils.bitmapToMat(outputBitmap, src)
        val graySrc = Mat()
        Imgproc.cvtColor(src, graySrc, Imgproc.COLOR_BGR2GRAY)
        val laplacian = Mat()
        Imgproc.Laplacian(graySrc, laplacian, CvType.CV_64F)

        // Calculate the variance of the Laplacian
        val variance = Mat()
        Core.multiply(laplacian, laplacian, variance)
        val mean = Core.mean(variance)
        return mean.`val`[0]
    }

    // 히스토그램 평쥰화 (명도 평준화)
    private fun histogramEqual(bitmap: Bitmap, equalizationRatio: Double = 0.4): Bitmap {
        // val output = Mat()
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val image = Mat()
        Utils.bitmapToMat(outputBitmap, image)

        if (image.channels() == 4) { // Color image
            val ycrcbArray = Mat()
            Imgproc.cvtColor(image, ycrcbArray, Imgproc.COLOR_RGB2YCrCb)

            val channels = ArrayList<Mat>(3)
            Core.split(ycrcbArray, channels)

            val equalizedChannel = Mat()
            Imgproc.equalizeHist(channels[0], equalizedChannel)

            Core.addWeighted(
                channels[0], 1 - equalizationRatio,
                equalizedChannel, equalizationRatio,
                0.0, channels[0]) // 원본 채널과 평활화된 채널을 혼합

            Core.merge(channels, ycrcbArray)
            Imgproc.cvtColor(ycrcbArray, image, Imgproc.COLOR_YCrCb2RGB)

        } else { // Gray image
            val equalizedImage = Mat()
            Imgproc.equalizeHist(image, equalizedImage)
            Core.addWeighted(
                image, 1 - equalizationRatio,
                equalizedImage, equalizationRatio,
                0.0, image) // 원본 이미지와 평활화된 이미지를 혼합
        }

        Utils.matToBitmap(image, outputBitmap)

        return outputBitmap
    }

    // 이미지 회전 helper
    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}