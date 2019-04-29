package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import com.troystopera.jkode.Component
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.statements.Declaration
import com.troystopera.jkode.vars.Array2DVar
import com.troystopera.jkode.vars.ArrayVar
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal object DeclarationProvider : StatementProvider(ProviderType.DECLARATION) {

    override fun populate(parent: CodeBlock, scope: GenScope, context: GenContext) {
        var count = 0
        val arrays = context.topics.contains(ProblemTopic.ARRAY)
        val arrays2d = context.topics.contains(ProblemTopic.ARRAY_2D)

        // declare an array if needed
        if (arrays) {
            parent.add(declareArray(scope, context))
            parent.add(declareInt(scope, context))
            count += 2
        }

        if (arrays2d) {
            parent.add(declare2DArray(scope, context))
            parent.add(declareInt(scope, context))
            count =+ 2
        }

        //continue declaring until random end
        while (count < MIN_DECLARATIONS || (count < MAX_DECLARATIONS && context.random.randHardBool())) {
            if (arrays && context.random.randHardBool())
                parent.add(declareArray(scope, context))
            else if (arrays2d && context.random.randHardBool())
                parent.add(declare2DArray(scope, context))
            else
                parent.add(declareInt(scope, context))
            count++
        }
    }

    private fun declareInt(scope: GenScope, context: GenContext): Declaration<*> {
        val name = context.variableProvider.nextVar()
        scope.addVar(name, VarType.INT)
        return Declaration(VarType.INT, name, IntVar[context.random.simpleInt()].asEval())
//        return Declaration(
//                VarType.INT,
//                name,
                // TODO: allow for declarations to be something other than an int literal
//                IntVar[context.random.simpleInt()].asEval()
//        )
    }

    private fun declareArray(scope: GenScope, context: GenContext): Declaration<*> {
        val name = context.variableProvider.nextVar()
        val length = context.random.randInt(MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH)
        scope.addArrVar(name, VarType.ARRAY[VarType.INT], length)
        return Declaration(
                VarType.ARRAY[VarType.INT],
                name,
                ArrayVar<IntVar>(
                        VarType.INT,
                        Array(length, {getRandInt(context)})).asEval()
        )
    }

    private fun declare2DArray(scope: GenScope, context: GenContext): Declaration<*> {
        val name = context.variableProvider.nextVar()
        val rowNum = context.random.randInt(MIN_2DARRAY_LENGTH, MAX_2DARRAY_LENGTH)
        val colNum = context.random.randInt(MIN_2DARRAY_LENGTH, MAX_2DARRAY_LENGTH)
        scope.add2DArrVar(name, VarType.ARRAY2D[VarType.INT], rowNum, colNum)
        return Declaration(
                VarType.ARRAY2D[VarType.INT],
                name,
                Array2DVar<IntVar>(
                        VarType.INT,
                        Array(rowNum, {Array(colNum, {getRandInt(context)})})).asEval()
        )
    }

    private fun getRandInt(context: GenContext): IntVar? {
       return IntVar[context.random.simpleInt()]
    }
}