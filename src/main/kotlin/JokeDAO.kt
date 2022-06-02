package com.campspot.dao

import AbstractJokeDAO
import Joke
import com.campspot.dao.entities.CampspotModel
import liquibase.pro.packaged.E
import org.hibernate.query.Query

class JokeDAO : AbstractJokeDAO<Joke>() {
    fun findById(id: Long): E? {
        val query: Query<*> = currentSession().createQuery(
            "SHOW COLUMNS FROM sys.joke"
        )
        query.setParameter("id", id)
        return getOne(query, "No postStaySurvey model found with id: $id")
    }
}
