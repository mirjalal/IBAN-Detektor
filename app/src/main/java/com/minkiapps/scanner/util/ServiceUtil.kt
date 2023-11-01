package com.minkiapps.scanner.util

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability

fun Context.isHmsAvailable() : Boolean =
    HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this) == ConnectionResult.SUCCESS

fun Context.isGmsAvailable() : Boolean =
    GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == com.google.android.gms.common.ConnectionResult.SUCCESS
