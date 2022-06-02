import com.campspot.AggregatorErrorType
import com.campspot.AggregatorObjectMapperFactory
import com.campspot.HelloWorldApplication.Companion.objectMapperNoFailOnUnknownProperties
import com.campspot.HelloWorldApplication.Companion.serviceBeans
import com.campspot.common.core.kotlin.exporters.apiexporter.APIExporterCommand
import com.campspot.common.core.kotlin.exporters.enumexporter.EnumExporterCommand
import com.fasterxml.jackson.databind.DeserializationFeature
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.eclipse.jetty.server.session.SessionHandler
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.joda.time.DateTimeZone
import com.campspot.jdbi3.DAOManager
import com.campspot.jdbi3.TransactionApplicationListener
import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.migrations.MigrationsBundle
import java.io.File
import java.util.*

class AggregatorApplication {

    fun initialize(bootstrap: Bootstrap<HelloWorldConfiguration>?) {

        setupCLICommands(bootstrap!!)

        bootstrap.addBundle(migrationsBundle())

        bootstrap.objectMapper = AggregatorObjectMapperFactory.newObjectMapper(TIME_ZONE, AggregatorErrorType::class.java)

        objectMapperNoFailOnUnknownProperties = bootstrap.objectMapper.copy()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        initializeLocalEnvironment(objectMapperNoFailOnUnknownProperties)

    }

    fun run(
        configuration: HelloWorldConfiguration,
        environment: Environment
    ){

        val daoManager = setupDAOManager(
            environment = environment,
            configuration = configuration,
            timezone = TIME_ZONE
        )

        val apiResourceBeans = setUpAPIResources(configuration, serviceBeans)
        registerAPIResources(
            environment = environment,
            apiResourceBeans = apiResourceBeans
        )
        serviceBeans = setUpServices(
            daoManager = daoManager,
            environment = environment,
            configuration = configuration,
        )

        environment.servlets().setSessionHandler(SessionHandler())
    }




    private fun registerAPIResources(
        environment: Environment,
        apiResourceBeans: com.campspot.Beans
    ) {
        apiResourceBeans.allBeans()
            .forEach {
                environment.jersey().register(it)
            }
    }

    private fun setupCLICommands(bootstrap: Bootstrap<HelloWorldConfiguration>) {
        bootstrap.addCommand(APIExporterCommand())
        bootstrap.addCommand(EnumExporterCommand())
    }

    companion object {
        private val TIME_ZONE = DateTimeZone.UTC.toTimeZone()
        const val MASTER = "master"
        lateinit var LOCAL_ENVIRONMENT: String

        fun setUpAPIResources(
            configuration: HelloWorldConfiguration,
            serviceBeans: com.campspot.Beans
        ): com.campspot.Beans {
            return com.campspot.Beans.plantNewBeanstalk()
                .bean(
                    Resource(
                        serviceBeans[Service::class]
                    )
                )
                .harvestBeans()
        }

        fun setUpServices(
            daoManager: MyDAOManager,
            environment: Environment,
            configuration: HelloWorldConfiguration
        ): com.campspot.Beans {
            val beanstalk = com.campspot.Beans.plantNewBeanstalk()

            return beanstalk
                .bean(
                    Service(
                        helloWorldConfiguration = configuration
                    )
                )
                .harvestBeans()
        }

        fun setupDAOManager(
            environment: Environment,
            configuration: HelloWorldConfiguration,
            timezone: TimeZone
        ): MyDAOManager {
            val jdbi = JdbiFactory()
                .build(environment, configuration.dataSourceFactory, "jdbi")
                .installPlugin(KotlinPlugin())
                .installPlugin(KotlinSqlObjectPlugin())

            val daoManager = MyDAOManager(jdbi)

            setUpJDBIListeners(
                daoManager = daoManager,
                jdbi = jdbi,
                environment = environment
            )

            return daoManager
        }

        private fun setUpJDBIListeners(
            daoManager: DAOManager,
            jdbi: Jdbi,
            environment: Environment
        ) {
            val transactionApplicationListener = TransactionApplicationListener(daoManager)
            transactionApplicationListener.registerDbi(MASTER, jdbi)

            environment.jersey().register(transactionApplicationListener)
        }

        private fun migrationsBundle(): MigrationsBundle<HelloWorldConfiguration> {
            return object : MigrationsBundle<HelloWorldConfiguration>() {
                override fun getDataSourceFactory(configuration: HelloWorldConfiguration): DataSourceFactory {
                    return configuration.dataSourceFactory
                }
            }
        }

        private fun initializeLocalEnvironment(objectMapper: ObjectMapper) {
            val localEnvironmentFile = File("../local-environment.json")

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
