import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

class HelloWorldConfiguration(
        @JsonProperty @NotEmpty val defaultString: String = "Default String"
        ): Configuration() {

    @Valid
    @JsonProperty("passThroughErrors")
    val passThroughErrors = true
    constructor(): this("")


}