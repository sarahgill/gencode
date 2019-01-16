package com.troystopera.jkode.exec.override

import com.troystopera.jkode.exec.CallStack
import com.troystopera.jkode.exec.Executable
import kotlin.reflect.KClass

class Watchable<E : Executable<*>> private constructor(
        private val base: String,
        private val callers: List<String>
) : CallStack() {

    //cached string to avoid O(n) toString
    private val string = super.toString()

    override fun getBase() = base

    override fun getCallers() = callers

    constructor(override: KClass<E>, vararg callers: KClass<Executable<*>>) :
            this(classToString(override), callers.map { classToString(it) })

    constructor(override: Class<E>, vararg callers: Class<Executable<*>>) :
            this(classToString(override.kotlin), callers.map { classToString(it.kotlin) })

    override fun toString(): String = string

    companion object {

        fun execToString(executable: Executable<*>): String = classToString(executable::class)

        private fun classToString(clazz: KClass<*>): String =
                clazz.qualifiedName ?: clazz.simpleName ?: clazz.java.name

    }

}