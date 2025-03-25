package com.dna.beyoureyes.ui.assign

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dna.beyoureyes.data.api.SpringApiResponseHandler
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.data.api.interceptor.AuthInterceptor
import com.dna.beyoureyes.data.api.model.ApiStatus
import com.dna.beyoureyes.data.model.Allergen
import com.dna.beyoureyes.data.model.Disease
import com.dna.beyoureyes.data.api.request.JoinRequest
import com.dna.beyoureyes.data.api.request.ProfileRequest
import com.dna.beyoureyes.data.model.Allergen.Companion.toMap
import com.dna.beyoureyes.data.model.Disease.Companion.toMap
import com.dna.beyoureyes.data.repository.AuthRepositoryImpl
import com.dna.beyoureyes.di.SpringClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import kotlin.coroutines.resume


class AssignViewModel : ViewModel() {

    // 기존 정보 있으면 가져오기(참조 가져오는 게 아닌 값 복사!!)
    private var _name : String? = AppUser.info?.name // 이름
    private var _gender : Int? = AppUser.info?.gender // 성별
    private var _birth : LocalDate? = AppUser.info?.birth // 생년월일
    private var _diseaseSet : MutableSet<Disease>? =
        AppUser.info?.disease?.toMutableSet() // 보유 질환 set
    private var _allergenSet : MutableSet<Allergen>? =
        AppUser.info?.allergens?.toMutableSet() // 보유 알레르기 set

    // 읽기 전용 변수
    val name : String? get() = _name
    val gender : Int? get() = _gender
    val birth : LocalDate? get() = _birth
    val diseaseSet : MutableSet<Disease>? get() = _diseaseSet
    val allergenSet : MutableSet<Allergen>? get() = _allergenSet

    // 이름 유효성 sealed class 및 변수 정의
    sealed class NameValidation { // 이름 유효성 검사 결과용 sealed class
        data object Empty : NameValidation()
        data object Duplicate : NameValidation()
        data object Valid : NameValidation()
        data object Error : NameValidation()
    }
    private val _nameValidationResult = MutableLiveData<NameValidation>() // 이름 유효성 검사 결과
    val nameValidationResult: LiveData<NameValidation> = _nameValidationResult
    private val _nameValidationMessage = MutableLiveData<String>() // 이름 유효성 검사 메세지
    val nameValidationMessage: LiveData<String> = _nameValidationMessage

    val birthDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun contains(disease: Disease): Boolean {
        return diseaseSet?.contains(disease) == true
    }

    fun contains(allergen: Allergen): Boolean {
        return _allergenSet?.contains(allergen) == true
    }

    fun addToAllergenSet(allergen: Allergen) {
        _allergenSet?.add(allergen) ?:run { _allergenSet = mutableSetOf(allergen) }
    }

    fun removeFromAllergenSet(allergen: Allergen) {
        _allergenSet?.remove(allergen)
    }

    fun clearAllergenSet() {
        _allergenSet?.clear()
    }

    fun addToDiseaseSet(disease: Disease) {
        _diseaseSet?.add(disease) ?:run { _diseaseSet = mutableSetOf(disease) }
    }

    fun removeFromDiseaseSet(disease: Disease) {
        _diseaseSet?.remove(disease)
    }

    fun clearDiseaseSet() {
        _diseaseSet?.clear()
    }

    fun setBirth(year: Int, month:Int, day:Int) {
        _birth = LocalDate.of(year, month, day)
    }

    fun setGender(gender: Int) {
        _gender = gender
    }

    fun setName(name: String) {
        _name = name
    }

    fun validateName(name: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _nameValidationResult.value = NameValidation.Empty
                _nameValidationMessage.value = "닉네임을 입력해주세요."
            } else if (name == AppUser.info?.name) { // 수정 시 사용자의 원래 이름에서 변경 사항 없을 때
                _nameValidationResult.value = NameValidation.Valid
                _nameValidationMessage.value = "현재 사용중인 닉네임입니다."
            } else {
                SpringApiResponseHandler {
                    SpringClient.noAuthSpringApi.checkNickname(name)
                }.onSuccess { isValidate, status ->
                    when(status) {
                        ApiStatus.SUCCESS -> { // 사용 가능
                            if(isValidate == true){
                                _nameValidationResult.value = NameValidation.Valid
                                _nameValidationMessage.value = "사용 가능한 닉네임입니다."
                            }else{
                                _nameValidationResult.value = NameValidation.Duplicate
                                _nameValidationMessage.value = "중복된 이름입니다. 다시 설정해주세요!"
                            }
                        } else -> { }
                    }
                }.onError { _ -> // 닉네임 중복 검사 중 오류 발생 시
                    _nameValidationResult.value = NameValidation.Error
                    _nameValidationMessage.value = "중복 확인 중 오류가 발생했습니다. 재시도 후에도 오류가 지속되면 앱을 다시 시작해주세요."
                }.execute()
            }
        }
    }

    // 기존 유저 정보 수정사항 업데이트
    suspend fun updateProfile(authRepository: AuthRepositoryImpl): ApiStatus = suspendCancellableCoroutine { continuation ->
        try {
            // 액세스 토큰 인증을 위해 AuthInterceptor 설정
            SpringClient.initAuthClient(AuthInterceptor(authRepository))

            // 사용자 싱글톤 객체 업데이트 및 변경값 감지
            val changedName : String? = _name?.let {
                if (it != AppUser.info?.name) {
                    AppUser.info?.name = it
                    it
                } else { null }
            }
            val changedGender : Int? = _gender?.let {
                if (it != AppUser.info?.gender) {
                    AppUser.info?.gender = it
                    it
                } else { null }
            }
            val changedBirth : String? = _birth?.let {
                if (it != AppUser.info?.birth) {
                    AppUser.info?.birth = it
                    try {
                        _birth!!.format(birthDateFormatter)
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Failed to convert BIRTH(${_birth}) to String")
                    }
                } else { null }
            }

            // 수정된 정보 요청 객체 만들기
            val request = ProfileRequest(
                userBirth = changedBirth,
                userGender = changedGender,
                userNickname = changedName
            )
            // 프로필 수정 API 호출
            viewModelScope.launch {
                SpringApiResponseHandler {
                    SpringClient.authSpringApi.updateProfile(request)
                }.onSuccess { _, status ->
                    continuation.resume(status)
                }.onError { status ->
                    continuation.resume(status)
                }.execute()
            }
        } catch (e: IllegalArgumentException) {
            Log.e("ASSIGN_VIEWMODEL", "$e")
            continuation.resume(ApiStatus.UNKNOWN)
        }
    }

    suspend fun updateAllergens(authRepository: AuthRepositoryImpl): ApiStatus = suspendCancellableCoroutine { continuation ->
        viewModelScope.launch {
            // 액세스 토큰 인증을 위해 AuthInterceptor 설정
            SpringClient.initAuthClient(AuthInterceptor(authRepository))

            // 사용자 싱글톤 객체 업데이트
            _allergenSet?.let { allergenSet ->
                AppUser.info?.allergens = allergenSet.ifEmpty { null }
            }?:run{ // 결과 없음 - 에러 처리
                Log.e("ASSIGN_VIEWMODEL", "'_allergenSet' is Null")
                continuation.resume(ApiStatus.UNKNOWN)
            }
            // 수정된 정보 요청 Map 만들기
            val allergenMapToSend = allergenSet?.toMap() ?: emptyMap()
            // 알러지 정보 수정 API 호출
            SpringApiResponseHandler {
                SpringClient.authSpringApi.updateAllergens(allergenMapToSend)
            }.onSuccess { _, status ->
                continuation.resume(status)
            }.onError { status ->
                continuation.resume(status)
            }.execute()
        }
    }

    suspend fun updateDisease(authRepository: AuthRepositoryImpl): ApiStatus = suspendCancellableCoroutine { continuation ->
        viewModelScope.launch {
            // 액세스 토큰 인증을 위해 AuthInterceptor 설정
            SpringClient.initAuthClient(AuthInterceptor(authRepository))

            // 사용자 싱글톤 객체 업데이트
            _diseaseSet?.let { diseaseSet ->
                AppUser.info?.disease = diseaseSet.ifEmpty { null }
            }?:run{ // 결과 없음 - 에러 처리
                Log.e("ASSIGN_VIEWMODEL", "'_diseaseSet' is Null")
                continuation.resume(ApiStatus.UNKNOWN)
            }
            // 수정된 정보 요청 Map 만들기
            val diseaseMapToSend = diseaseSet?.toMap() ?: emptyMap()
            // 질환 정보 수정 API 호출
            SpringApiResponseHandler {
                SpringClient.authSpringApi.updateDisease(diseaseMapToSend)
            }.onSuccess { _, status ->
                continuation.resume(status)
            }.onError { status ->
                continuation.resume(status)
            }.execute()
        }
    }

    // 신규 유저 가입 처리
    suspend fun registerUserInfo(authRepository: AuthRepositoryImpl): ApiStatus = suspendCancellableCoroutine { continuation ->
        try {
            // 사용자 싱글톤 객체 업데이트
            AppUser.setInfo(
                name = _name
                    ?: throw IllegalArgumentException("Failed to set AppUser info: NAME value is NULL"),
                gender = _gender
                    ?: throw IllegalArgumentException("Failed to set AppUser info: GENDER value is NULL"),
                birth = _birth
                    ?: throw IllegalArgumentException("Failed to set AppUser info: BIRTH value is NULL"),
                disease = diseaseSet?.ifEmpty { null },
                allergens = allergenSet?.ifEmpty { null }
            )
            // 신규 가입 요청 객체 만들기
            val joinRequest = JoinRequest(
                deviceId = AppUser.id
                    ?: throw IllegalArgumentException("Failed to create JoinRequest: AppUser ID is NULL"),
                userBirth = try { _birth!!.format(birthDateFormatter) } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to create JoinRequest: can't convert LocalDate(${_birth}) to String")
                },
                userGender = _gender
                    ?: throw IllegalArgumentException("Failed to create JoinRequest: GENDER value is NULL"),
                userNickname = _name
                    ?: throw IllegalArgumentException("Failed to create JoinRequest: NAME value is NULL"),
                allergy = allergenSet?.toMap() ?: emptyMap(),
                disease = diseaseSet?.toMap() ?: emptyMap()
            )
            // 가입(사용자 정보 저장) 요청 후 액세스 토큰 반환 받기
            viewModelScope.launch {
                SpringApiResponseHandler {
                    SpringClient.noAuthSpringApi.join(joinRequest)
                }.onSuccess { data, status ->  // 응답 수신 성공
                    when(status) {
                        ApiStatus.SUCCESS -> { // 가입 성공 (액세스 토큰 수신 성공)
                            // 로그인 성공 (액세스 토큰 수신 성공)
                            val accessToken = data!!
                            authRepository.saveToken(accessToken)  // 토큰을 전용 Repository에 저장

                            // 액세스 토큰 인증을 위해 AuthInterceptor 설정
                            SpringClient.initAuthClient(AuthInterceptor(authRepository))
                        } else -> { } // 그 외 case 없음
                    }
                    continuation.resume(status)
                }.onError { status -> // 가입 실패
                    continuation.resume(status)
                }.execute()
            }
        } catch(e: IllegalArgumentException) {
            Log.e("ASSIGN_VIEWMODEL", "$e")
            continuation.resume(ApiStatus.UNKNOWN)
        }
    }
}