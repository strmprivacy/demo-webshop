package io.streammachine.demo

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.streammachine.demo.routes.api
import io.streammachine.demo.routes.statics
import io.streammachine.driver.client.StreamMachineClient
import io.streammachine.driver.domain.Config
import org.slf4j.LoggerFactory

private val log by lazy { LoggerFactory.getLogger("UltimateStoreApplicationKt") }

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.demoModule() {
    val streamMachineClient = StreamMachineClient.builder()
        .billingId(environment.config.property("store.streamMachine.billingId").getString())
        .clientId(environment.config.property("store.streamMachine.clientId").getString())
        .clientSecret(environment.config.property("store.streamMachine.clientSecret").getString())
        .config(Config.builder().build())
        .build()

    install(ContentNegotiation) {
        jackson {}
    }
    install(Sessions) {
        cookie<Session>(CookieNames.SESSION) {
            cookie.maxAgeInSeconds = 43200
            serializer = UltimateStoreSessionSerializer()
        }
    }
    install(StatusPages) {
        exception<Throwable> {
            log.error("An unexpected error occurred", it)

            context.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(listOf(it.javaClass.canonicalName, it.message))
            )
        }
    }

    routing {
        statics(streamMachineClient, environment.config)
        api(streamMachineClient, environment.config)
    }
}
