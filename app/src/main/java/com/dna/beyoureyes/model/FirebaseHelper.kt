package com.dna.beyoureyes.model

import android.util.Log
import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.dna.beyoureyes.AppUser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.Serializable

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

        suspend fun receiveUserData() : Boolean {
            var hasData = true
            val db = FirebaseFirestore.getInstance()
            return if (AppUser.id != null) {
                suspendCancellableCoroutine { continuation ->
                    db.collection("userInfo")
                        .whereEqualTo("userId", AppUser.id)
                        .get()
                        .addOnSuccessListener { info ->
                            var hasData = false
                            if (info.isEmpty) {
                                Log.d("INFO", "NO DATA FOUND")
                                continuation.resume(false) { }
                            } else {
                                for (document in info) {
                                    val userName = document.data["userName"] as String
                                    val userGender = (document.data["userGender"] as Long).toInt()
                                    val userBirth = document.data["userBirth"] as Timestamp
                                    val userDisease = document.data["userDisease"] as ArrayList<String>
                                    val userAllergy = document.data["userAllergy"] as ArrayList<String>
                                    val profile = document.data["userProfile"] as String
                                    AppUser.setInfo(userName, userGender, userBirth, userDisease, userAllergy, profile)
                                    hasData = true
                                }
                                continuation.resume(hasData) { }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("INFO", "Error fetching data", e)
                            continuation.resumeWith(Result.failure(e))
                        }
                }
            } else {
                false
            }
        }
    }
}