package aze.talmir.ibandetektor.id

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class IDResult(
    val idNumber: String,
    val issuingCountry: String,
    val givenNames: String,
    val sureName: String,
    val birthDate: LocalDate,
    val expirationDate: LocalDate,
    val nationality: String,
    val gender: String?,
    val nameNeedCorrection: Boolean,
    val scannedAddress : String?
) : Parcelable
