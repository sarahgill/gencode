package com.troystopera.gencode.generator

import com.troystopera.gencode.ProblemTopic
import java.util.*
import kotlin.collections.HashSet

sealed class NestStructure : Pattern.NestPattern() {

    class NestedLoop(val depth: Int) : NestStructure()

    class NestedConditional(val depth: Int) : NestStructure()

    class NestedLoopConditional(val depth: Int) : NestStructure()

    companion object {

        object SingleLoop : NestStructure()

        object SingleConditional : NestStructure()

        object ComboLoopConditional : NestStructure()

        fun get(topics: HashSet<out ProblemTopic>, difficulty: Double, random: Random): NestStructure {
            return when {
                topics.contains(ProblemTopic.ARRAY_2D) -> when {
                    difficulty <= 1.0 -> NestedLoop(1)
                    else -> NestedLoop(2)
                }
                topics.contains(ProblemTopic.WHILE) -> when {
                    difficulty <= 1.0 -> SingleLoop
                    else -> SingleLoop
                }
                topics.contains(ProblemTopic.FOR_LOOP) -> when {
                    topics.contains(ProblemTopic.ARRAY) -> when {
                        difficulty < 0.50 -> SingleLoop
                        else -> NestedLoop(2)
                    }
                    topics.contains(ProblemTopic.ARRAY_2D) -> when {
                        difficulty <= 1.0 -> NestedLoop(1)
                        else -> NestedLoop(2)
                    }
                    topics.contains(ProblemTopic.CONDITIONAL) -> {
                        if (difficulty < 0.25) ComboLoopConditional
                        else NestedLoopConditional(if (difficulty < 0.75) 2 else 3)
                    }
                    else ->
                        if (difficulty < 0.5) {
                            SingleLoop
                        } else {
                            NestedLoop(2)
                        }
                }
                topics.contains(ProblemTopic.CONDITIONAL) -> when {
                    difficulty < 0.50 -> SingleConditional
                    difficulty < 0.90 -> NestedConditional(2)
                    else -> NestedConditional(if (random.nextBoolean()) 2 else 3)
                }
                else -> get(hashSetOf(ProblemTopic.FOR_LOOP), difficulty, random)
            }
        }

    }

}
