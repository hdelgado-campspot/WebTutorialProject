package com.campspot

import kotlin.reflect.KClass

class Beans(inputBeans: Set<Any>) {
    private val beans: Map<KClass<*>, Any> = inputBeans
        .map { it::class to it }
        .toMap()

    operator fun <T : Any> get(beanType: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return (beans[beanType] ?: throw RuntimeException(
            "Bean of type $beanType was not found"
        )) as T
    }

    fun allBeans(): Set<Any> {
        return beans.values.toSet()
    }

    class Beanstalk {
        private val beans = mutableSetOf<Any>()

        operator fun <T : Any> get(beanType: KClass<T>): T {
            @Suppress("UNCHECKED_CAST")
            return beans.first { it::class == beanType } as T
        }

        fun <T> bean(bean: T): Beanstalk {
            beans.add(bean as Any)
            return this
        }

        fun harvestBeans(): Beans {
            return Beans(beans)
        }
    }

    companion object {
        /*
        Create a builder-style beanstalk.

        val beans = Beans.plantNewBeanstalk()
          .bean(...)
          .bean(...)
          .harvestBeans()
         */
        fun plantNewBeanstalk(): Beanstalk {
            return Beanstalk()
        }
    }
}