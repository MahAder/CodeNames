package com.ader.codenames.domain.utils

import kotlin.random.Random

object GameUtils {
    fun random(maxValue: Int): Int {
        val until = maxValue + 1
        return Random.nextInt(0, until)
    }
}