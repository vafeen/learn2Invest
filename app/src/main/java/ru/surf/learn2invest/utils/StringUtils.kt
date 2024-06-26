package ru.surf.learn2invest.utils

fun String.isThisContainsSequenceOrEmpty(): Boolean {
    for (number in 0..6) {
        if (contains(
                "$number${number + 1}${number + 2}${number + 3}"
            ) || isEmpty()
        ) {
            return true
        }
    }
    return false
}

fun String.isThisContains3NumbersOfEmpty(): Boolean {
    if (this == "") {
        return true
    }
    for (number in 0..9) {
        if (contains("$number".repeat(3))) {
            return true
        }
    }
    return false
}

