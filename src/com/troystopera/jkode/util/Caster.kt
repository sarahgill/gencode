package com.troystopera.jkode.util

import kotlin.reflect.full.safeCast

internal object Caster {

    internal fun <T : Any> safeCast(instance: T, any: Any): T? = instance::class.safeCast(any)

}