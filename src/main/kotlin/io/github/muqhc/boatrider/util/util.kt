package io.github.muqhc.boatrider.util

fun Double.format(digits: Int) = "%.${digits}f".format(this)