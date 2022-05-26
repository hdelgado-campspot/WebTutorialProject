
import com.codahale.metrics.annotation.Timed
import java.util.concurrent.atomic.AtomicLong
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class Resource(
    private val service: Service
        ) {

    @GET
    fun print() {
        println("Hellooooo")
    }
    private val counter = AtomicLong()
    @GET
    @Path("/hello")
    @Timed
    fun getString(@QueryParam("string") string: String?): String {
        return service.takeString(string)
    }

}