package com.troystopera.gencode.generator

/* Generation */
internal const val MAX_NESTING_DEPTH = 3

/* Conditional */
internal const val MIN_BRANCHES = 2
internal const val MAX_BRANCHES = 4

/* Declaration */
internal const val MIN_DECLARATIONS = 2
internal const val MAX_DECLARATIONS = 3
internal const val MIN_ARRAY_LENGTH = 4
internal const val MAX_ARRAY_LENGTH = 8
internal const val MIN_2DARRAY_LENGTH = 2
internal const val MAX_2DARRAY_LENGTH = 5

/* Manipulation */
internal const val MIN_OPERATIONS = 1
//TODO increase manipulation constraints
internal const val MAX_OPERATIONS = 1

/* Random Operations */
internal const val THRESHOLD_OP_TYPE_MOD = 0.7