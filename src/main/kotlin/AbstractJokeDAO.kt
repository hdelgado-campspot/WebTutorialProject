import io.dropwizard.hibernate.AbstractDAO
import liquibase.pro.packaged.T

abstract class AbstractJokeDAO<Any>: AbstractDAO<Any>() {
    override fun persist(entity: Any): Any {
        return super.persist(entity)
    }

}