package com.troystopera.gencode.generator

sealed class Pattern {

    abstract class NestPattern : Pattern()

    data class Array2DWalk(val arrayName: String, val rowIndex: String, val colIndex: String): Pattern()

    data class ArrayWalk(val arrayName: String, val index: String) : Pattern()

    data class LoopSkipManip(val up: Boolean, val intName: String) : Pattern()

}