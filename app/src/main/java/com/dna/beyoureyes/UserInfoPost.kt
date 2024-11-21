package com.dna.beyoureyes

import com.google.firebase.firestore.FirebaseFirestore

class UserInfoPost {

    val db = FirebaseFirestore.getInstance()

    val user = hashMapOf(
        AppUser.info?.name = ,
        AppUser.info?.gender = ,
        AppUser.info?.birth = ,

    )

}