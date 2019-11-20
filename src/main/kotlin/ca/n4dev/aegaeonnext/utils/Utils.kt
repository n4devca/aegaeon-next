package ca.n4dev.aegaeonnext.utils

/**
 *
 * Utils.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 23 - 2019
 *
 */

const val DASH: String = "-"

fun <T> requireNonNull(value: T?, pExceptionCreator : () -> Exception) : T {
    if (value != null) {
        return value;
    }
    throw pExceptionCreator.invoke();
}


fun asString(pObject: Any?): String {

    return if (pObject != null) {

        if (pObject is String) {
            pObject
        } else {
            pObject.toString()
        }
    } else DASH
}



fun join(vararg pStrings: String): String {
    return pStrings.joinToString(" ")
}