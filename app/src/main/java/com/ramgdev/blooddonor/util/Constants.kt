package com.ramgdev.blooddonor.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ramgdev.blooddonor.model.Donor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

var userCollectionRef = Firebase.firestore.collection("donors")
var storageReference = Firebase.storage.reference
var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
//var userRef = Firebase.firestore.collection("donors").document(user?.uid!!)
var firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

private fun saveUserDetails(donor: Donor) = CoroutineScope(Dispatchers.IO).launch {
    try {
        userCollectionRef.add(donor).await()
    } catch (e: Exception) {
        e.message
    }
}