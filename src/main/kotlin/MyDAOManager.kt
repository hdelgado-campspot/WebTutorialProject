
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import com.campspot.jdbi3.DAO
import com.campspot.jdbi3.DAOManager

open class MyDAOManager(
    val jdbi: Jdbi
) : DAOManager(jdbi) {
    fun getDAOInstancesValue(): HashMap<String, DAO> = daoInstances.get()
    fun getTransactionValue(): Handle = transaction.get()

    fun setValues(
        daoInstances: HashMap<String, DAO>,
        transaction: Handle
    ) {
        this.daoInstances.set(daoInstances)
        this.transaction.set(transaction)
    }
}



