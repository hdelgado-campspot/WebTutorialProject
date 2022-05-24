package com.campspot

import HelloWorldConfiguration
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import com.campspot.common.core.exceptions.dropwizard.registerExceptionMappers
import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import net.gini.dropwizard.gelf.filters.GelfLoggingFilter
import org.eclipse.jetty.server.session.SessionHandler
import org.hibernate.validator.constraints.Length
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.DispatcherType
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

class Representation(
        @JsonProperty val id: Long = 0,
        @JsonProperty @field:Length(max=3) val content: String = ""){

    constructor(): this(0, "")


}

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class Resource(
        private val str: String) {

    private val counter = AtomicLong()
    @GET
    @Timed
    fun getString(@QueryParam("string") string: Optional<String>): Representation {
        val res = String.format(string.orElse(str))
        return Representation(counter.incrementAndGet(), res)
    }

    @POST
    @Timed

    fun postString(@Valid str: Representation) = Representation(str.id, "String: ${str.content}")

}

class HelloWorldApplication : Application<HelloWorldConfiguration>() {

    override fun run(
        configuration: HelloWorldConfiguration,
        environment: Environment
    ) {
        val stringResult = Resource(configuration.defaultString)
        environment.jersey().register(stringResult)
        registerExceptionMappers(
            jersey = environment.jersey(),
            passThroughErrors = configuration.passThroughErrors
        )
        environment.servlets()
            .addFilter("request-logs", GelfLoggingFilter())
            .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")

//        serviceBeans = setUpServices(
//            environment = environment,
//            configuration = configuration
//        )
        environment.servlets().setSessionHandler(SessionHandler())
    }

    companion object {
//        lateinit var serviceBeans: Beans
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            HelloWorldApplication().run(*args)
        }
    }
}