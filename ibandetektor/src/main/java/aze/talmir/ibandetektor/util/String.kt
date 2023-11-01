package aze.talmir.ibandetektor.util

fun String.replaceWithinRange(fromIndex: Int, toIndex : Int, toFind: String, replaceWith: String) : String =
    substring(0, fromIndex) + substring(fromIndex, toIndex).replace(toFind, replaceWith) + substring(toIndex)

fun String.replaceWithinRange(fromIndex: Int, toFind: String, replaceWith :String) : String =
    replaceWithinRange(fromIndex, length, toFind, replaceWith)
