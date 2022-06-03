package com.campspot.dao

import Joke
import com.campspot.jdbi3.DAO
import io.dropwizard.hibernate.AbstractDAO

class JokeDAO : DAO, AbstractDAO<Joke>() {
    public override fun persist(entity: Joke): Joke {
        return super.persist(entity)
    }
}
