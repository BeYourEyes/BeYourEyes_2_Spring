package com.dna.beyoureyes.model

import android.util.Log
import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.dna.beyoureyes.AppUser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class FirebaseHelper {
    companion object {
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        // 데이터를 Firestore에 추가
        fun sendData(userInfo: HashMap<String, Any?>, collectionName: String) {
            firestore.collection(collectionName)
                .add(userInfo)
                .addOnSuccessListener { documentReference ->
                    Log.d("FIREBASE :", "SUCCESS added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE :", "Error adding document", e)
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

        fun receiveUserData() : Boolean {
            var hasData = true
            val db = Firebase.firestore
            if (AppUser.id != null) {
                db.collection("userInfo")
                    .whereEqualTo("userId", AppUser.id)
                    .get()
                    .addOnSuccessListener { info ->
                        if (info.isEmpty) {
                            Log.d("FIREBASE : ", "NO DATA FOUND")
                            hasData = false
                            return@addOnSuccessListener
                        }
                        for (document in info) {
                            val userName = document.data.get("userName") as String
                            val userGender = document.data.get("userGender") as Long
                            val userBirth = document.data.get("userBirth") as Timestamp
                            val userDisease = document.data.get("userDisease") as ArrayList<String>
                            val userAllergy = document.data.get("userAllergy") as ArrayList<String>
                            val profile = document.data.get("userProfile") as String
                            AppUser.setInfo(userName, userGender.toInt(), userBirth, userDisease, userAllergy, profile)
                        }
                    }
            }
            else { hasData = false }
            return hasData
        }
    }
}