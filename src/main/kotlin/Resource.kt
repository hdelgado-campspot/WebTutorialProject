
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

    @GET
    @Path("/hello")
    @Timed
    fun getString(@QueryParam("string") string: String?): String {
        return service.takeString(string)
    }

    @POST
    @Path("/create")
    @Timed
    fun checkJoke(@QueryParam("string") type: String,
                   @QueryParam("string") setup: String, @QueryParam("string") punchline: String?): Joke {

        return service.createJoke(type, setup, punchline)
    }
}