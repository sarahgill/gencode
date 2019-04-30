package com.troystopera.gencode.generator.components

import com.troystopera.gencode.generator.GenContext
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.ProviderType
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.WhileLoop
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.evaluations.MathOperation
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.vars.VarType
import com.troystopera.jkode.statements.Assignment


internal object WhileLoopProvider : ComponentProvider(ProviderType.WHILE_LOOP) {
    override fun generate(scope: GenScope, context: GenContext): Result {
        var varName = scope.getRandVar(VarType.INT)!!
        // variable in the condition should not be the one being returned
        if (varName == context.mainIntVar) {
            varName = scope.getRandVar(VarType.INT)!!
        }
        val newScope = scope.createChildScope(WhileLoop::class)
        val up = context.random.randBool()

        val loop = WhileLoop(
                generateCondition(context, varName, up),
                generateExecution(context, varName, up))

        return Result(loop, arrayOf(loop), newScope)
    }

    fun generateCondition(context: GenContext, varName: String, up: Boolean): Comparison<IntVar> {
        var type: Comparison.Type
        if (up) {
            if (context.random.randBool()) {
                type = Comparison.Type.LESS_THAN
            } else type = Comparison.Type.LESS_THAN_EQUAL_TO
        }
        else {
            if (context.random.randBool()) {
                type = Comparison.Type.GREATER_THAN
             } else type = Comparison.Type.GREATER_THAN_EQUAL_TO
        }

        val value = IntVar[context.random.randInt(0, 15)].asEval()
        return Comparison(type, Variable(VarType.INT, varName), value)
    }

    fun generateExecution(context: GenContext, varName: String, up: Boolean): Assignment {
        val varVariable = Variable(VarType.INT, varName)
        return when {
            up -> {
                Assignment(
                        varName,
                        MathOperation(MathOperation.Type.ADD, varVariable, IntVar[context.random.randInt(1, 2)].asEval())
                )
            }
            else -> Assignment(
                    varName,
                    MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[context.random.randInt(1, 2)].asEval())
            )
        }
    }
}