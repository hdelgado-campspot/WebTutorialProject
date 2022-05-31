package com.campspot

import com.campspot.common.core.dates.jackson.CampTimeModule
import com.campspot.common.core.exceptions.api.ErrorType
import com.campspot.common.core.middleware.money.MoneyModule
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import io.dropwizard.jackson.Jackson
import java.util.TimeZone

// Have to take a factory approach instead of just extending
// because ObjectMapper can't be extended in Kotlin - there are
// conflicts with the copy method that Jackson defines
class AggregatorObjectMapperFactory {
    companion object {
        fun <T : ErrorType> newObjectMapper(
            timeZone: TimeZone,
            extErrorType: Class<T>
        ): ObjectMapper {
            val objectMapper = Jackson.newObjectMapper()

            objectMapper.setTimeZone(timeZone)
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            objectMapper.registerModule(KotlinModule())
            objectMapper.registerModule(ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            objectMapper.registerModule(errorTypeModule(extErrorType))
            objectMapper.registerModule(MoneyModule())
            objectMapper.registerModule(CampTimeModule())

            return objectMapper
        }

        private fun <T : ErrorType> errorTypeModule(extErrorType: Class<T>): SimpleModule {
            val resolver = SimpleAbstractTypeResolver()
                .addMapping(ErrorType::class.java, extErrorType)

            val abstractTypeMapperModule = SimpleModule(
                "AbstractTypeMapper",
                Version.unknownVersion()
            )
            abstractTypeMapperModule.setAbstractTypes(resolver)

            return abstractTypeMapperModule
        }
    }
}