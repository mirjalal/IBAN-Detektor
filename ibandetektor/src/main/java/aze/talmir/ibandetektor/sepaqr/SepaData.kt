package aze.talmir.ibandetektor.sepaqr

data class SepaData(val recipient : String, val iban : String, val bic : String, val usage : String, val amount : Double)
