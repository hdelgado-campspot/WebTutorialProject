import org.jdbi.v3.sqlobject.statement.SqlQuery

interface RuleDAO {

    @SqlQuery(
        """
        SELECT * FROM sys.joke
        """
    )
    fun getJokeTableInfo(): List<String>
}