package com.campspot


import com.campspot.common.core.exceptions.api.ErrorType
import com.fasterxml.jackson.annotation.JsonValue
import javax.ws.rs.core.Response

enum class AggregatorErrorType(override val errorCode: Int) : ErrorType {
    EXTERNAL_SERVICE_ERROR(0),
    WEBSOCKET_CLIENT_ID_NOT_FOUND(1),
    INVALID_PARK_ID(2),
    INVALID_PARK_SLUG(3),
    SEARCH_REQUEST_DATE_RANGE_TOO_WIDE(4),
    INVALID_GUEST_CATEGORIES(5),
    INVALID_CAMPSITE_TYPE_ID(6),
    CAMPSPOT_USER_NOT_AUTHENTICATED(7),
    INVALID_RECAPTCHA_VERIFICATION_TOKEN(8),
    SHOPPING_CART_NOT_FOUND_ERROR(9),
    CONFIRMED_RESERVATION_MODIFICATION(10),
    INVALID_CAMPSPOT_CLIENT(11),
    INVALID_PASSWORD_REQUIREMENTS(12),
    INVALID_RV_INFO_SPECIFICATIONS(13),
    ADD_SITE_ERROR_NO_LONGER_AVAILABLE(14),
    EDIT_SITE_ERROR_NO_LONGER_AVAILABLE(15),
    ADD_ADDON_ERROR_NO_LONGER_AVAILABLE(16),
    EDIT_ADDON_ERROR_NO_LONGER_AVAILABLE(17),
    OPTIMIZATION_SERVICE_TIMEOUT_ERROR(18),
    OPTIMIZATION_NO_LONGER_AVAILABLE(19),
    INVALID_RESERVATION_REQUEST_DATES(20),
    SHOPPING_CART_REFRESH_CAMPSITE_PRICING_ERROR(21),
    POST_STAY_SURVEY_ERROR(22),
    GEO_LOCATION_NOT_FOUND_ERROR(23),
    ADDRESS_NOT_FOUND_ERROR(23),
    STATE_CODE_NOT_FOUND_ERROR(24),
    INVOICE_NOT_FOUND_ERROR(24),
    INVALID_STANDARDIZATION_ERROR(25);

    override fun getStatus(): Int {
        return when (this) {
            EXTERNAL_SERVICE_ERROR -> Response.Status.INTERNAL_SERVER_ERROR.statusCode
            WEBSOCKET_CLIENT_ID_NOT_FOUND -> Response.Status.NOT_FOUND.statusCode
            CAMPSPOT_USER_NOT_AUTHENTICATED -> Response.Status.UNAUTHORIZED.statusCode
            CONFIRMED_RESERVATION_MODIFICATION -> Response.Status.FORBIDDEN.statusCode
            INVALID_CAMPSPOT_CLIENT, INVALID_PASSWORD_REQUIREMENTS -> Response.Status.BAD_REQUEST.statusCode
            SHOPPING_CART_NOT_FOUND_ERROR, GEO_LOCATION_NOT_FOUND_ERROR, ADDRESS_NOT_FOUND_ERROR, INVOICE_NOT_FOUND_ERROR -> Response.Status.NOT_FOUND.statusCode
            INVALID_RV_INFO_SPECIFICATIONS -> 423
            INVALID_GUEST_CATEGORIES -> 424
            INVALID_STANDARDIZATION_ERROR -> 425
            else -> 422
        }
    }

    override fun isReservationError(): Boolean {
        return false
    }

    @JsonValue
    override fun toString(): String {
        return "GATOR-$errorCode"
    }

    companion object {
        @JvmStatic
        fun fromString(errorCode: String): AggregatorErrorType {
            return when {
                errorCode.startsWith("GATOR-") -> {
                    values().firstOrNull {
                        it.errorCode == errorCode.substring("GATOR-".length).toInt()
                    } ?: EXTERNAL_SERVICE_ERROR
                }
                else -> EXTERNAL_SERVICE_ERROR
            }
        }
    }
}
