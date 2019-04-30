package com.troystopera.gencode.generator.components

import com.sun.tools.javah.Gen
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.constraints.ForLoopConstraints
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.evaluations.*
import com.troystopera.jkode.statements.Assignment
import com.troystopera.jkode.statements.Declaration
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal object ForLoopProvider : ComponentProvider(ProviderType.FOR_LOOP) {

    override fun generate(scope: GenScope, context: GenContext): Result {
        val varName = context.variableProvider.nextVar()
        var set2DPattern = false
        val newScope = scope.createChildScope(ForLoop::class)
        var pattern = createPattern(varName, newScope, context)
        val varName2 = context.variableProvider.nextVar()
        if (context.topics.contains(ProblemTopic.ARRAY_2D)) {
            pattern = createPattern(varName, varName2, newScope, context)
            newScope.addVar(varName2, VarType.INT, false)
        }
        if (pattern != null) {
            newScope.addPattern(pattern)
            if (pattern is Pattern.ArrayWalk) {
                context.mainArray = pattern.arrayName
            } else if (pattern is Pattern.Array2DWalk){
                set2DPattern = true
                context.mainArray = pattern.arrayName
            }
        }
        newScope.addVar(varName, VarType.INT, false)

        var col = false
        val up = ForLoopConstraints.useIncrease(context.random, pattern)
        val loop = ForLoop(
                genDeclaration(varName, up, context, pattern, scope, col),
                genComparison(varName, up, context, pattern, scope, col),
                genAssignment(varName, up, context, scope, pattern)
        )
        if (newScope.hasPattern(Pattern.Array2DWalk::class) && set2DPattern) {
            col = true
            val innerLoop = ForLoop(
                    genDeclaration(varName2, up, context, pattern, scope, col),
                    genComparison(varName2, up, context, pattern, scope, col),
                    genAssignment(varName2, up, context, scope, pattern)
            )
            loop.add(innerLoop)
        }

        return Result(loop, arrayOf(loop), newScope)
    }

    fun createPattern(intName: String, scope: GenScope, context: GenContext): Pattern? {
        // array walk
        if ((scope.hasVarType(VarType.ARRAY[VarType.INT])) && !scope.hasPattern(Pattern.ArrayWalk::class)
                && context.topics.contains(ProblemTopic.ARRAY)) {
            val array = scope.getRandVar(VarType.ARRAY[VarType.INT])!!
            return Pattern.ArrayWalk(array, intName)
        }

        return null
    }

    fun createPattern(rowIntName: String, colIntName: String, scope: GenScope, context: GenContext): Pattern? {
        if (scope.hasVarType(VarType.ARRAY2D[VarType.INT]) && !scope.hasPattern(Pattern.Array2DWalk::class)
            && context.topics.contains(ProblemTopic.ARRAY_2D)) {
            val array2d = scope.getRandVar(VarType.ARRAY2D[VarType.INT])!!
            return Pattern.Array2DWalk(array2d, rowIntName, colIntName)
        }

        return null
    }

    private fun genDeclaration(varName: String, up: Boolean, context: GenContext, pattern: Pattern?, scope: GenScope, col: Boolean): Declaration<IntVar> {
        // TODO utilize other variables in loop declaration
        val value: Evaluation<IntVar> = when (pattern) {
            //array walk declaration if you are working with arrays and for_loops this sets it = 0 if up or array length if down
            is Pattern.ArrayWalk -> {
                if (up) IntVar[0].asEval()
                else MathOperation(MathOperation.Type.SUBTRACT, ArrayLength(Variable(VarType.ARRAY, pattern.arrayName)), IntVar[1].asEval())
            }

            is Pattern.Array2DWalk -> {
                if (up) IntVar[0].asEval()
                else if (col) IntVar[scope.getArr2DColLength(pattern.arrayName) - 1].asEval()
                else MathOperation(MathOperation.Type.SUBTRACT, Array2DSize(Variable(VarType.ARRAY2D, pattern.arrayName)), IntVar[1].asEval())
            }
            //default declaration
            //increase the else randInt to make larger iterations
            else -> IntVar[if (up)
                context.random.randInt(0, 2)
            else if (context.random.difficulty < 0.25)
                context.random.randInt(1, 5)
            else if (context.random.difficulty < 0.50)
                context.random.randInt(5, 10)
            else if (context.random.difficulty < 0.75)
                context.random.randInt(3, 3)
            else context.random.randInt(4, 5)].asEval()
        }


        return Declaration(VarType.INT, varName, value)
    }
    // TODO make sure gen comparison doesnt equal gen declaration var
    private fun genComparison(varName: String, up: Boolean, context: GenContext, pattern: Pattern?, scope: GenScope, col: Boolean): Comparison<IntVar> {
        val type: Comparison.Type = when (pattern) {
        //array walk comparison
            is Pattern.ArrayWalk -> {
                if (up) Comparison.Type.LESS_THAN
                else Comparison.Type.GREATER_THAN_EQUAL_TO
            }
            is Pattern.Array2DWalk -> {
                if (up) Comparison.Type.LESS_THAN
                else Comparison.Type.GREATER_THAN_EQUAL_TO
            }
        //default comparison
            else -> {
                if (up)
                    if (context.random.randBool()) Comparison.Type.LESS_THAN else Comparison.Type.LESS_THAN_EQUAL_TO
                else
                    if (context.random.randBool()) Comparison.Type.GREATER_THAN else Comparison.Type.GREATER_THAN_EQUAL_TO
            }
        }

        val value: Evaluation<IntVar> = when (pattern) {
        //array walk values
            is Pattern.ArrayWalk -> {
                if (up) ArrayLength(Variable(VarType.ARRAY, pattern.arrayName))
                else IntVar[0].asEval()
            }
            is Pattern.Array2DWalk -> {
                if (col && up) IntVar[scope.getArr2DColLength(pattern.arrayName)].asEval()
                else if (up) Array2DSize(Variable(VarType.ARRAY2D, pattern.arrayName))
                else IntVar[0].asEval()
            }
            else -> IntVar[if (up) {

                if (context.random.difficulty < 0.25)
                    context.random.randInt(3, 5)
                else if (context.random.difficulty < .50)
                     context.random.randInt(5,10)
                else if(context.random.difficulty < .75)
                     context.random.randInt(3,3)
                else
                    context.random.randInt(4,5)
            }
            else context.random.randInt(0, 2)].asEval()
        }
        return Comparison(type, Variable(VarType.INT, varName), value)
    }

    private fun genAssignment(varName: String, up: Boolean, context: GenContext, scope: GenScope, pattern: Pattern?): Assignment {
        val varVariable = Variable(VarType.INT, varName)
        return when {
            pattern is Pattern.ArrayWalk -> {
                if (up) Assignment(varName, MathOperation(MathOperation.Type.ADD, varVariable, IntVar[1].asEval()))
                else Assignment(varName, MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[1].asEval()))
            }
            pattern is Pattern.Array2DWalk -> {
                if (up) Assignment(varName, MathOperation(MathOperation.Type.ADD, varVariable, IntVar[1].asEval()))
                else Assignment(varName, MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[1].asEval()))
            }
        //add or subtract by 1
            ForLoopConstraints.useSingleStep(context.random, scope) -> {
                if (up) Assignment(varName, MathOperation(MathOperation.Type.ADD, varVariable, IntVar[1].asEval()))
                else Assignment(varName, MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[1].asEval()))
            }
        //add or subtract by a random number
            else -> {
                if (up) Assignment(
                        varName,
                        MathOperation(MathOperation.Type.ADD, varVariable, IntVar[context.random.randInt(1, 2)].asEval())
                )
                else Assignment(
                        varName,
                        MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[context.random.randInt(1, 2)].asEval())
                )
            }
        }
    }

}