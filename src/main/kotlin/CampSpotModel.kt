package com.campspot.dao.entities

import java.io.Serializable
import java.lang.reflect.InvocationTargetException
import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class CampspotModel<T : CampspotModel<T>> : Serializable, Comparable<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun compareTo(o: T): Int {
        return if (id != null && o!!.id != null) {
            id!!.compareTo(o.id!!)
        } else if (id == null && o!!.id != null) {
            -1
        } else if (id != null) {
            1
        } else {
            0
        }
    }

    companion object {
        fun <T : CampspotModel<*>?> wrapJustId(id: Long, ModelClass: Class<T>): T {
            Objects.requireNonNull(id, "To wrap an ID it must not be null.")
            val model: T
            return try {
                model = ModelClass.getDeclaredConstructor().newInstance()
                model!!.id = id
                model
            } catch (e: InstantiationException) {
                throw RuntimeException(
                    "Unable to instantiate CampspotModel class $ModelClass does it have a zero args constructor?",
                    e
                )
            } catch (e: IllegalAccessException) {
                throw RuntimeException(
                    "Unable to instantiate CampspotModel class $ModelClass does it have a zero args constructor?",
                    e
                )
            } catch (e: InvocationTargetException) {
                throw RuntimeException(
                    "Unable to instantiate CampspotModel class $ModelClass does it have a zero args constructor?",
                    e
                )
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(
                    "Unable to instantiate CampspotModel class $ModelClass does it have a zero args constructor?",
                    e
                )
            }
        }
    }
}