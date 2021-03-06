package com.campspot

import HelloWorldConfiguration
import LocalEnvironment
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import com.campspot.common.core.exceptions.dropwizard.registerExceptionMappers

import net.gini.dropwizard.gelf.filters.GelfLoggingFilter
import org.eclipse.jetty.server.session.SessionHandler
import java.util.*
import javax.servlet.DispatcherType
import Resource
import Service
import com.campspot.common.core.exceptions.tracing.setupClientTracing
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jackson.Jackson
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import org.glassfish.jersey.client.rx.guava.RxListenableFutureInvokerProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.joda.time.DateTimeZone
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import javax.ws.rs.client.Client


class HelloWorldApplication : Application<HelloWorldConfiguration>() {

    private val TIME_ZONE = DateTimeZone.UTC.toTimeZone()
    override fun initialize(bootstrap: Bootstrap<HelloWorldConfiguration>?) {
        bootstrap!!.addBundle(migrationsBundle())
        bootstrap!!.objectMapper = AggregatorObjectMapperFactory.newObjectMapper(TIME_ZONE,
            AggregatorErrorType::class.java)

        objectMapperNoFailOnUnknownProperties = bootstrap!!.objectMapper.copy()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        initializeLocalEnvironment(objectMapperNoFailOnUnknownProperties)
    }

    override fun run(
        configuration: HelloWorldConfiguration,
        environment: Environment
    ) {
        setUpExternalServiceClient(environment, configuration)
        registerExceptionMappers(
            jersey = environment.jersey(),
            passThroughErrors = configuration.passThroughErrors
        )
        environment.servlets()
            .addFilter("request-logs", GelfLoggingFilter())
            .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")

        serviceBeans = setUpServices(
            environment = environment,
            configuration = configuration
        )
        val apiResourceBeans = setUpAPIResources(configuration, serviceBeans)
        registerAPIResources(
            environment = environment,
            apiResourceBeans = apiResourceBeans
        )
        environment.servlets().setSessionHandler(SessionHandler())
    }

    private fun migrationsBundle(): MigrationsBundle<HelloWorldConfiguration> {
        return object : MigrationsBundle<HelloWorldConfiguration>() {
            override fun getDataSourceFactory(configuration: HelloWorldConfiguration): DataSourceFactory {
                return configuration.dataSourceFactory
            }
        }
    }

    companion object {
        lateinit var objectMapperNoFailOnUnknownProperties: ObjectMapper
        lateinit var LOCAL_ENVIRONMENT: String
        lateinit var serviceBeans: Beans
        lateinit var externalServiceClient: Client
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            HelloWorldApplication().run(*args)
        }

        fun setUpServices(
            environment: Environment,
            configuration: HelloWorldConfiguration
        ): Beans {
            val beanstalk = Beans.plantNewBeanstalk()

            return beanstalk
                .bean(
                    Service(
                        helloWorldConfiguration = configuration
                    )
                )
                .harvestBeans()
        }

        fun setUpAPIResources(
            configuration: HelloWorldConfiguration,
            serviceBeans: Beans
        ): Beans {
            return Beans.plantNewBeanstalk()
                .bean(
                    Resource(
                        serviceBeans[Service::class]
                    )
                )
                .harvestBeans()
        }
        fun setUpExternalServiceClient(environment: Environment, configuration: HelloWorldConfiguration) {
            val logger = Logger.getLogger("CLIENT")
//        logger.addHandler(JerseyClientLogHandler())

            externalServiceClient = JerseyClientBuilder(environment)
                .using(configuration.jerseyClientConfiguration)
                .build("external-service-client")
                .register(LoggingFeature(logger, Level.INFO, LoggingFeature.DEFAULT_VERBOSITY, null))
                .register(RxListenableFutureInvokerProvider::class.java)

            setupClientTracing(externalServiceClient)
        }
        private fun registerAPIResources(
            environment: Environment,
            apiResourceBeans: Beans
        ) {
            apiResourceBeans.allBeans()
                .forEach {
                    environment.jersey().register(it)
                }
        }

        private fun initializeLocalEnvironment(objectMapper: ObjectMapper) {
            val localEnvironmentFile = File("local-environment.json")

            LOCAL_ENVIRONMENT = if (localEnvironmentFile.isFile) {
                val localEnvironment =
                    objectMapper.readValue(localEnvironmentFile.readText().trim(), LocalEnvironment::class.java)

                localEnvironment.email
            } else {
                val environment = LocalEnvironment(
                    email = ""
                )

                val json = objectMapper.writeValueAsString(environment)

                localEnvironmentFile.writeText(json)

                json
            }
        }

    }
}