package com.ramgdev.blooddonor.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ramgdev.blooddonor.model.Donor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val userCollectionRef = Firebase.firestore.collection("donors")
private var storageReference = Firebase.storage.reference

private fun saveUserDetails(donor: Donor) = CoroutineScope(Dispatchers.IO).launch {
    try {
        userCollectionRef.add(donor).await()
    } catch (e: Exception) {
        e.message
    }
}
