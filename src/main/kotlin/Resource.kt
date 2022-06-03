
import com.campspot.HelloWorldApplication.Companion.MASTER
import com.campspot.jdbi3.InTransaction
import com.codahale.metrics.annotation.Timed
import java.util.concurrent.atomic.AtomicLong
import javax.validation.Valid
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

    @GET
    @Path("/hello")
    @Timed
    fun getString(@QueryParam("string") string: String?): String {
        return service.takeString(string)
    }

    @POST
    @Path("/create")
    @Timed
    @InTransaction(name = MASTER)
    fun checkJoke(@Valid jokeRequest: Joke): Joke {

        return service.createJoke(jokeRequest.type, jokeRequest.setup, jokeRequest.punchline)
    }
}