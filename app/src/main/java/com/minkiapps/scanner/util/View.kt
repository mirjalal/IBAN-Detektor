package com.minkiapps.scanner.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.os.Build
import java.io.Serializable

inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }

inline fun <reified T : Serializable> Activity.extraSerializableOrThrow(key : String) = lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        intent?.getSerializableExtra(key, T::class.java) ?: throw RuntimeException("no extra found for key $key in intent")
    else
        intent?.getSerializableExtra(key) as T? ?: throw RuntimeException("no extra found for key $key in intent")
}

fun Context.isPortrait() : Boolean {
   val orientation = resources.configuration.orientation
   return orientation == Configuration.ORIENTATION_PORTRAIT
}
