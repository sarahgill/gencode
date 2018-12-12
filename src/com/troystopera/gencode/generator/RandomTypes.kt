package com.troystopera.gencode.generator

import com.troystopera.jkode.evaluations.MathOperation
import java.util.*

object RandomTypes {

    fun operationType(difficulty: Double, random: Random): MathOperation.Type {
        val maxIndex = when {
            difficulty >= THRESHOLD_OP_TYPE_MOD -> 5
            else -> 4
        }
        return MathOperation.Type.values()[random.nextInt(maxIndex)]
    }
}