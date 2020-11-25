package io.streammachine.demo

internal data class ErrorResponse(val errors: List<String?>)

object CookieNames {
    const val CUSTOMER_ID = "customerid"
    const val CONSENT_LEVEL = "consentlevel"
    const val SESSION = "SESSION"
}

enum class ConsentLevel(val id: Int?) {
    REQUIRED(null),
    ANALYTICAL(0),
    MARKETING(1);

    companion object : CaseInsensitiveEnum<ConsentLevel>(values()) {
        fun getConsentLevels(consentLevel: String?) =
            if (!consentLevel.isNullOrBlank()) {
                caseInsensitiveValueOf(consentLevel).id?.let {
                    (0..it).toList()
                } ?: listOf()
            } else {
                listOf()
            }
    }
}

enum class Environment {
    PRODUCTION,
    DEVELOPMENT;

    companion object : CaseInsensitiveEnum<Environment>(values())
}

abstract class CaseInsensitiveEnum<T>(private val values: Array<T>) where T : Enum<T> {
    fun caseInsensitiveValueOf(value: String) =
        values.firstOrNull { it.name.equals(value, ignoreCase = true) }
            ?: throw IllegalArgumentException("No such element '$value'")
}
