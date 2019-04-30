package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.GenerationException
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.constraints.ForLoopConstraints
import com.troystopera.gencode.generator.constraints.ManipulationConstraints
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.components.WhileLoop
import com.troystopera.jkode.evaluations.ArrayAccess
import com.troystopera.jkode.evaluations.Array2DAccess
import com.troystopera.jkode.evaluations.MathOperation
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.statements.ArrayAssign
import com.troystopera.jkode.statements.Array2DAssign
import com.troystopera.jkode.statements.Assignment
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal object ManipulationProvider : StatementProvider(ProviderType.MANIPULATION) {

    override fun populate(parent: CodeBlock, scope: GenScope, context: GenContext) {
        if (!scope.hasVarType(VarType.INT))
            throw GenerationException(IllegalStateException("No ints in scope passed to ManipulationProvider"))

        var count = 0

        //start by checking for an array generation pattern
        if (scope.hasPattern(Pattern.ArrayWalk::class) || scope.hasPattern(Pattern.Array2DWalk::class)) {
            if (ManipulationConstraints.useDirectManipulation(context.random)) {
                if (context.topics.contains(ProblemTopic.ARRAY_2D)) {
                    val array2DWalk = scope.getPattern(Pattern.Array2DWalk::class)!! as Pattern.Array2DWalk

                    parent.add(Array2DAssign(
                      Variable(VarType.ARRAY2D[VarType.INT], array2DWalk.arrayName),
                            Variable(VarType.INT, array2DWalk.rowIndex),
                            Variable(VarType.INT, array2DWalk.colIndex),
                            Variable(VarType.INT, array2DWalk.rowIndex)
                            // Variable(VarType.INT, scope.getRandVar(VarType.INT)
                    ))
                } else {
                    val arrayWalk = scope.getPattern(Pattern.ArrayWalk::class)!! as Pattern.ArrayWalk
                    parent.add(ArrayAssign(
                            Variable(VarType.ARRAY[VarType.INT], arrayWalk.arrayName),
                            Variable(VarType.INT, arrayWalk.index),
                            Variable(VarType.INT, arrayWalk.index)
                    ))
                }
                count++
            } else {
                //manipulate an int that may be used by the array assign
                val assign = forLoopManip(context, scope)
                parent.add(assign)
                if (context.topics.contains(ProblemTopic.ARRAY_2D)) {
                    val array2DWalk = scope.getPattern(Pattern.Array2DWalk::class)!! as Pattern.Array2DWalk
                    // parent.add(genArray2DManipulation(null, scope, context, parent))
                    parent.add(Array2DAssign(
                            Variable(VarType.ARRAY2D[VarType.INT], array2DWalk.arrayName),
                            Variable(VarType.INT, array2DWalk.rowIndex),
                            Variable(VarType.INT, array2DWalk.colIndex),
                            Variable(VarType.INT, assign.varName)
                            // Variable(VarType.INT, scope.getRandVar(VarType.INT)
                    ))
                } else {
                    val arrayWalk = scope.getPattern(Pattern.ArrayWalk::class)!! as Pattern.ArrayWalk
                    parent.add(ArrayAssign(
                            Variable(VarType.ARRAY[VarType.INT], arrayWalk.arrayName),
                            Variable(VarType.INT, arrayWalk.index),
                            Variable(VarType.INT, assign.varName)
                    ))
                }
                count += 2
            }
        }

        if (context.topics.contains(ProblemTopic.WHILE)) {
            val whileLoop = parent as WhileLoop
            parent.add(whileLoop.assignment)
        } else if (!scope.hasPattern(Pattern.ArrayWalk::class) && !scope.hasPattern(Pattern.Array2DWalk::class) && context.mainIntVar != null) {
            if (scope.isIn(ForLoop::class)) {
                if (ForLoopConstraints.haveMultipleStatements(context.random)) {
                    val manip = forLoopManip2(context, scope)
                    if (manip != null) {
                        parent.add(manip)
                        parent.add(forLoopManip(context, scope, manip.varName))
                    } else {
                        parent.add(forLoopManip(context, scope))
                    }
                } else {
                    parent.add(forLoopManip(context, scope))
                }
            }
            else
                parent.add(Assignment(context.mainIntVar!!, genIntEvaluation(context, scope, context.mainIntVar!!)))
            count++
        }

        while (!context.topics.contains(ProblemTopic.WHILE) && (count < MIN_OPERATIONS || (count < MAX_OPERATIONS && context.random.randHardBool()))) {
            val manipulateVar = scope.getRandVar(VarType.INT)!!
            // potentially manipulate an array with 33% probability
            if (context.random.randBool(.33) && context.topics.contains(ProblemTopic.ARRAY) && scope.hasVarType(VarType.ARRAY[VarType.INT])) {
                parent.add(genArrayManipulation(null, scope, context))
            } else if (context.topics.contains(ProblemTopic.ARRAY_2D) && scope.hasVarType(VarType.ARRAY2D[VarType.INT])) {
                parent.add(genArray2DManipulation(null, scope, context, parent))
            }
            // standard int manipulation
            else
                parent.add(Assignment(manipulateVar, genIntEvaluation(context, scope, manipulateVar)))
            count++
        }
    }

    // TODO consolidate array manipulations
    private fun genArrayManipulation(i: Evaluation<IntVar>?, scope: GenScope, context: GenContext): ArrayAssign<*> {
        val arr = scope.getRandVar(VarType.ARRAY[VarType.INT])!!
        val index = i ?: IntVar[context.random.randEasyInt(0, scope.getArrLength(arr) - 1)].asEval()

        return when {
            context.random.randBool() && scope.hasVarType(VarType.INT) -> {
                ArrayAssign(
                        Variable(VarType.ARRAY[VarType.INT], arr),
                        index,
                        Variable(VarType.INT, scope.getRandVar(VarType.INT)!!)
                )
            }
            else -> {
                val srcArr = scope.getRandVar(VarType.ARRAY[VarType.INT])!!
                val srcIndex = context.random.randEasyInt(0, scope.getArrLength(srcArr) - 1)
                ArrayAssign(
                        Variable(VarType.ARRAY[VarType.INT], arr),
                        index,
                        ArrayAccess(VarType.INT,
                                Variable(VarType.ARRAY[VarType.INT], srcArr),
                                IntVar[srcIndex].asEval()
                        )
                )
            }
        }
    }

    private fun genArray2DManipulation(i: Evaluation<IntVar>?, scope: GenScope, context: GenContext, parent: CodeBlock): Array2DAssign<*> {
        val arr = scope.getRandVar(VarType.ARRAY2D[VarType.INT])!!
        val rowIndex = i ?: IntVar[context.random.randEasyInt(0, scope.getArr2DRowLength(arr) - 1)].asEval()
        val colIndex = i ?: IntVar[context.random.randEasyInt(0, scope.getArr2DColLength(arr) - 1)].asEval()

        if (ForLoopConstraints.haveMultipleStatements(context.random)) {
            val manip = forLoopManip2(context, scope)
            if (manip != null) {
                parent.add(manip)
                parent.add(forLoopManip(context, scope, manip.varName))
            } else {
                parent.add(forLoopManip(context, scope))
            }
        } else {
            parent.add(forLoopManip(context, scope))
        }

        return when {
            context.random.randBool() && scope.hasVarType(VarType.INT) -> {
                Array2DAssign(
                        Variable(VarType.ARRAY2D[VarType.INT], arr),
                        rowIndex, colIndex,
                        Variable(VarType.INT, scope.getRandVar(VarType.INT)!!)
                )
            } else -> {
                val srcArr = scope.getRandVar(VarType.ARRAY2D[VarType.INT])!!
                val srcRowIndex = context.random.randEasyInt(0, scope.getArr2DRowLength(srcArr) - 1)
                val srcColIndex = context.random.randEasyInt(0, scope.getArr2DColLength(srcArr) - 1)
                Array2DAssign(
                        Variable(VarType.ARRAY2D[VarType.INT], arr),
                        rowIndex, colIndex,
                        Array2DAccess(VarType.INT,
                                Variable(VarType.ARRAY2D[VarType.INT], srcArr),
                                IntVar[srcRowIndex].asEval(),
                                IntVar[srcColIndex].asEval()
                        )
                )
            }
        }
    }

    private fun forLoopManip(context: GenContext, scope: GenScope): Assignment {
        var opType = RandomTypes.operationType(context.random.difficulty, context.random)
        while (opType == MathOperation.Type.DIVIDE || opType == MathOperation.Type.MODULO) {  // TODO find a better fix for divide by 0 & % - allow it to be included here
            opType = RandomTypes.operationType(context.random.difficulty, context.random)
        }
        val op = MathOperation(
                opType,
                Variable(VarType.INT, scope.getRandVar(VarType.INT)!!),
                Variable(VarType.INT, scope.getRandUnmanipVar(VarType.INT)!!)
        )
        return Assignment(context.mainIntVar!!, op)
    }

    private fun forLoopManip(context: GenContext, scope: GenScope, variable: String): Assignment {
        var opType = RandomTypes.operationType(context.random.difficulty, context.random)
        while (opType == MathOperation.Type.DIVIDE || opType == MathOperation.Type.MODULO) {  // TODO find a better fix for divide by 0 & % - allow it to be included here
            opType = RandomTypes.operationType(context.random.difficulty, context.random)
        }
        val op = MathOperation(
                opType,
                Variable(VarType.INT, variable),
                Variable(VarType.INT, scope.getRandUnmanipVar(VarType.INT)!!)
        )
        return Assignment(context.mainIntVar!!, op)
    }

    private fun forLoopManip2(context: GenContext, scope: GenScope): Assignment? {
        var opType = RandomTypes.operationType(context.random.difficulty, context.random)
        while (opType == MathOperation.Type.DIVIDE || opType == MathOperation.Type.MODULO) {  // TODO find a better fix for divide by 0 & % - allow it to be included here
            opType = RandomTypes.operationType(context.random.difficulty, context.random)
        }
        val op = MathOperation(
                opType,
                Variable(VarType.INT, context.mainIntVar!!),
                Variable(VarType.INT, scope.getRandUnmanipVar(VarType.INT)!!)
        )
        /* make sure random variable is not the main var
           if it is, try to generate a different one
           if it is still the main var, probably no other options - return null
         */
        var variable = scope.getRandVar(VarType.INT)!!
        if (variable.equals(context.mainIntVar!!)) {
            variable = scope.getRandVar(VarType.INT)!!
            if (variable.equals(context.mainIntVar!!)) {
                return null
            }
        }
        return Assignment(variable, op)
    }
}