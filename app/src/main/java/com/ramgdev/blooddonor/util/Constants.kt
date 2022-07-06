package com.ramgdev.blooddonor.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
var firebaseAuth = FirebaseAuth.getInstance()
val userCollectionRef = Firebase.firestore.collection("donors")
var storageReference = Firebase.storage.reference