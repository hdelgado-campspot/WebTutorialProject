import io.dropwizard.hibernate.AbstractDAO
import liquibase.pro.packaged.E
import org.hibernate.query.Query
import javax.persistence.EntityNotFoundException


abstract class AbstractJokeDAO<T>: AbstractDAO<T>() {
    override fun persist(entity: T): T {
        return super.persist(entity)
    }

    @Throws(EntityNotFoundException::class)
    protected open fun getOne(query: Query<*>, notFoundMessage: String?): E? {
        return query.uniqueResult() as E ?: throw EntityNotFoundException(notFoundMessage)
    }
}