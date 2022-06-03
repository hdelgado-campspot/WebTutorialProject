package com.campspot.dao

import AbstractJokeDAO
import Joke
import com.campspot.jdbi3.DAO
import io.dropwizard.hibernate.AbstractDAO
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.BindFields
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface JokeDAO : DAO {
    @SqlUpdate(
        """
        INSERT INTO joke (type, setup, punchline) VALUES (:type, :setup, :punchline)
        """
    )
    fun insertJoke(@BindBean joke: Joke): Unit

    @SqlQuery(
        """
        SELECT * FROM joke WHERE type = ?
        """
    )
    fun getJokesByType(type: String): List<Joke>

}
