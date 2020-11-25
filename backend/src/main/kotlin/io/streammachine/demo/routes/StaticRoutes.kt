package io.streammachine.demo.routes

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.HttpHeaders.Referrer
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.streammachine.demo.ConsentLevel
import io.streammachine.demo.CookieNames
import io.streammachine.demo.Environment
import io.streammachine.demo.Session
import io.streammachine.driver.client.StreamMachineClient
import io.streammachine.driver.serializer.SerializationType
import io.streammachine.schemas.strmcatalog.clickstream.ClickstreamEvent
import io.streammachine.schemas.strmcatalog.clickstream.Customer
import io.streammachine.schemas.strmcatalog.clickstream.StrmMeta
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

private val log by lazy { LoggerFactory.getLogger("StaticRoutes") }

internal fun Route.statics(streamMachineClient: StreamMachineClient, config: ApplicationConfig) {
    val environment = Environment.caseInsensitiveValueOf(
        config.propertyOrNull("ktor.deployment.environment")?.getString() ?: "development"
    )

    intercept(ApplicationCallPipeline.Call) {
        if (call.request.cookies[CookieNames.SESSION] == null) {
            call.sessions.set(CookieNames.SESSION, Session(UUID.randomUUID().toString().replace("-", "")))
        }

        if (call.request.path() == "/store") {
            val event = createRenderEvent(call.request)
            streamMachineClient.send(event, SerializationType.AVRO_BINARY)
        }
    }

    static("/store") {
        if (Environment.DEVELOPMENT == environment) {
            // Make sure the working dir for your run configuration is set to the root of this git repo
            staticRootFolder = File("./frontend/build")
            file("/", "index.html")
            file("*", "index.html")
            static("static") {
                files("static")
            }
        } else {
            resource("/", "frontend/index.html")
            resource("*", "frontend/index.html")
            static("static") {
                resources("frontend/static")
            }
        }
    }
}

private fun createRenderEvent(request: ApplicationRequest): ClickstreamEvent? {
    return ClickstreamEvent.newBuilder()
        .setAbTests(emptyList())
        .setEventType("homepage-visit")
        .setCustomer(
            Customer.newBuilder()
                .setId(request.cookies[CookieNames.CUSTOMER_ID] ?: "ANONYMOUS")
                .build()
        )
        .setReferrer(request.headers[Referrer] ?: "${request.local.scheme}://${request.host()}${request.local.uri}")
        .setUserAgent(request.userAgent())
        .setProducerSessionId(request.cookies[CookieNames.SESSION] ?: "NO_SESSION")
        .setConversion(1)
        .setStrmMeta(
            StrmMeta.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSchemaId("clickstream")
                .setNonce(0)
                .setConsentLevels(ConsentLevel.getConsentLevels(request.cookies[CookieNames.CONSENT_LEVEL]))
                .build()
        )
        .setUrl("${request.local.scheme}://${request.host()}${request.local.uri}")
        .build()
}
