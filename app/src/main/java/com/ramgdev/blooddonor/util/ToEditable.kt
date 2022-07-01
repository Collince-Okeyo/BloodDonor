package com.ramgdev.blooddonor.util

import android.content.Context
import android.nfc.Tag
import android.text.Editable

interface ToEditable {

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun show (application: Context, tag: String)
}