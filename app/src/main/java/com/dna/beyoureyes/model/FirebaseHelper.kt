package com.dna.beyoureyes.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class FirebaseHelper {
    companion object {
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        // 데이터를 Firestore에 추가
        fun sendData(userInfo: HashMap<String, Serializable?>, collectionName: String) {
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
    }
}