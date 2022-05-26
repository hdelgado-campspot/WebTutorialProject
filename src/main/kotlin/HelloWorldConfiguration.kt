import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.client.JerseyClientConfiguration
import io.dropwizard.db.DataSourceFactory
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class HelloWorldConfiguration : Configuration() {

    @Valid
    @NotNull
    @JsonProperty("database")
    val dataSourceFactory = DataSourceFactory()

    val defaultString: String = "Default String"

    @Valid
    @JsonProperty("passThroughErrors")
    val passThroughErrors = true

    @Valid
    @NotNull
    @JsonProperty("jerseyClient")
    val jerseyClientConfiguration = JerseyClientConfiguration()
}