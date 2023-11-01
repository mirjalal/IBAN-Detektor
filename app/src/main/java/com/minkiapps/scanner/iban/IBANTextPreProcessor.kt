package com.minkiapps.scanner.iban

import com.minkiapps.scanner.util.replaceWithinRange
import java.util.Locale

object IBANTextPreProcessor {
    fun preProcess(raw : String): String =
        raw.uppercase(Locale.ENGLISH)
            .replace(" ", "")
            .replaceWithinRange(2, 4,"O", "0")
}