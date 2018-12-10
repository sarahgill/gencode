package com.troystopera.gencode.generator.distractors

import com.troystopera.gencode.Problem
import com.troystopera.gencode.ProblemTopic
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.exec.Executor
import com.troystopera.jkode.exec.override.OverrideExecutor
import com.troystopera.jkode.exec.override.Watchable
import com.troystopera.jkode.vars.*
import java.util.*

class DistractorGenerator(private val problem: Problem) {

    private val random = Random()
    private val topics = problem.topics.toHashSet()
    lateinit var overrideExec: OverrideExecutor

    fun getDistractors(count: Int) = genDistractors(count, problem.mainFunction.returnType.NULL)

    private inline fun <reified T : JVar<*>> genDistractors(count: Int, nullVar: T): List<String> {
        val distractors = mutableSetOf<String>()
        val correct = Executor().execute(problem.mainFunction).getReturnVar() as? T

        // now try comparison overrides
        overrideExec = OverrideExecutor()
        overrideExec.addOverride(Watchable(Comparison::class), ComparisonOverride.OrEqualToMistake)
        var result = overrideExec.execute(problem.mainFunction).getReturnVar() as? T
        if (result != null && correct != null) {
            if (result.toString() != correct.toString()) {
                distractors.add(result.toString())
            }
        }

        // will likely get stuck in an infinite loop if executed for for loop
        if (!topics.contains(ProblemTopic.FOR_LOOP)) {
            overrideExec = OverrideExecutor()
            overrideExec.addOverride(Watchable(Comparison::class), ComparisonOverride.MisreadSignMistake)
            result = overrideExec.execute(problem.mainFunction).getReturnVar() as? T
            if (result != null && correct != null) {
                if (result.toString() != correct.toString()) {
                    distractors.add(result.toString())
                }
            }

            overrideExec = OverrideExecutor()
            overrideExec.addOverride(Watchable(Comparison::class), ComparisonOverride.InvertedBoolean)
            result = overrideExec.execute(problem.mainFunction).getReturnVar() as? T
            if (result != null && correct != null) {
                if (result.toString() != correct.toString()) {
                    distractors.add(result.toString())
                }
            }
        } else {
            overrideExec = OverrideExecutor()
            overrideExec.addOverride(Watchable(ForLoop::class), ForLoopOverride.SkipFirst)
            result = overrideExec.execute(problem.mainFunction).getReturnVar() as? T
            if (result != null && correct != null) {
                if (result.toString() != correct.toString()) {
                    distractors.add(result.toString())
                }
            }
        }

        // ensure the number of distractors is sufficient
        if (distractors.size < count) {
            val newDistractors = when (correct) {
                is IntVar -> fillList(count, correct, distractors)
                else -> TODO("take into consideration return types other than int")
            }
            distractors.addAll(newDistractors)
        }

        // ensure the number of distractors is not too high
        return distractors.take(count)
    }

    private fun fillList(count: Int, correct: IntVar, currentDistractors: MutableSet<String>): MutableList<String> {
        val strings = mutableListOf<String>()
        var delta = 1 + random.nextInt(10) + correct.toString().toInt()
        var size = currentDistractors.size

        while (size < count) {
            val temp = (count + delta).toString()
            if (temp != correct.toString()) {
                strings.add((count + delta).toString())
                size++
            }
            delta += 1 + random.nextInt(10)
            delta *= -1
        }
        return strings
    }
}