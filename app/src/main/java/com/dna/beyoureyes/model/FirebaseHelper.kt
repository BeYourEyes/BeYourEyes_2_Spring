package com.dna.beyoureyes.model

import android.util.Log
import com.dna.beyoureyes.AppUser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.Serializable
import kotlinx.coroutines.tasks.await

class FirebaseHelper {
    companion object {
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        // 데이터를 Firestore에 추가
        fun sendData(userInfo: HashMap<String, Any?>, collectionName: String) {
            firestore.collection(collectionName)
                .add(userInfo)
                .addOnSuccessListener { documentReference ->
                    Log.d("INFO", "SUCCESS added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("INFO", "Error adding document", e)
                }
        }

        // 데이터를 Firestore에서 삭제
        fun deleteData(userId: String, collectionName: String, onSuccess: () -> Unit) {
            firestore.collection(collectionName)
                .whereEqualTo("userID", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // 찾은 문서를 삭제
                        firestore.collection(collectionName)
                            .document(document.id)
                            .delete()
                            .addOnCompleteListener {
                                Log.d("REGISTERFIRESTORE : ", "DELETE SUCCESS")
                                // 삭제 완료 시 onSuccess 호출
                                onSuccess()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("REGISTERFIRESTORE : ", "Error deleting documents.", exception)
                }
        }
        suspend fun receiveUserData(currentUser: FirebaseUser): Boolean {
            val db = Firebase.firestore
            return try {
                    val info = db.collection("userInfo")
                        .whereEqualTo("userId", currentUser.uid)
                        .get()
                        .await()
                    if (info.isEmpty) {
                        Log.d("RECEIVE_USER_DATA", "NO DATA FOUND")
                        false
                    } else {
                        Log.d("RECEIVE_USER_DATA", "DATA FOUND")
                        for (document in info) {
                            val userName = document.data.get("userName") as String
                            val userGender = document.data.get("userGender") as Long
                            val userBirth = document.data.get("userBirth") as Timestamp
                            val userDisease = document.data.get("userDisease") as ArrayList<String>
                            val userAllergy = document.data.get("userAllergy") as ArrayList<String>
                            val profile = document.data.get("userProfile") as String?
                            AppUser.setInfo(userName, userGender.toInt(), userBirth, userDisease, userAllergy, profile)

                            // 프로필 사진 uri 로드하여 저장
                            profile?.let{
                                Log.d("RECEIVE_USER_DATA", "${profile}")
                                try {
                                    val storageRef = com.google.firebase.Firebase.storage.reference.child(profile)
                                    storageRef.downloadUrl.await()?.let { // 비동기 작업을 동기적으로 기다림
                                        AppUser.setProfileImgUri(it)
                                    }
                                } catch (exception : Exception) {
                                    Log.d("RECEIVE_USER_DATA", "Error getting documents: wrong image path", exception)
                                }

                            }

                        }
                        true
                    }
                } catch (exception: Exception) {
                    Log.d("RECEIVE_USER_DATA", "Error getting documents: ", exception)
                    false
                }
        }

        suspend fun checkDuplicateName(nickname: String): Boolean {
            val db = Firebase.firestore
            return try {
                val querySnapshot = db.collection("userInfo")
                    .whereEqualTo("userName", nickname)
                    .get()
                    .await()
                !querySnapshot.isEmpty // 결과가 비어있지 않으면 중복
            } catch (exception: Exception) {
                Log.e("REGISTERFIRESTORE", "Error checking nickname", exception)
                false // 오류 발생 시 기본값 반환
            }
        }
    }
}