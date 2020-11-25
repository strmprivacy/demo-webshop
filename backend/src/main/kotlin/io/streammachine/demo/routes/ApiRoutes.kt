package io.streammachine.demo.routes

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Referrer
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.streammachine.demo.ConsentLevel
import io.streammachine.demo.CookieNames
import io.streammachine.driver.client.StreamMachineClient
import io.streammachine.driver.serializer.SerializationType
import io.streammachine.schemas.strmcatalog.clickstream.ClickstreamEvent
import io.streammachine.schemas.strmcatalog.clickstream.Customer
import io.streammachine.schemas.strmcatalog.clickstream.StrmMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ViewportEvents(
    val entered: List<String>,
    val exited: List<String>
)

data class CartEvent(
    val product: String,
    val action: String
)

data class ModalProductEvent(
    val product: String,
    val action: String
)

internal fun Route.api(streamMachineClient: StreamMachineClient, config: ApplicationConfig) {
    route("/frontend-events") {
        post("/in-view") {
            val viewportEvents = call.receive<ViewportEvents>()

            sendTransitionEvent(viewportEvents.entered, "entered-viewport", streamMachineClient)
            sendTransitionEvent(viewportEvents.exited, "exited-viewport", streamMachineClient)

            call.respond(HttpStatusCode.OK)
        }
        post("/cart") {
            val cartEvent = call.receive<CartEvent>()

            sendTransitionEvent(listOf(cartEvent.product), "${cartEvent.action}-to-cart", streamMachineClient)
            call.respond(HttpStatusCode.OK)
        }
        post("/modal") {
            val modalProductEvent = call.receive<ModalProductEvent>()

            sendTransitionEvent(
                listOf(modalProductEvent.product),
                "${modalProductEvent.action}-modal",
                streamMachineClient
            )
            call.respond(HttpStatusCode.OK)
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.sendTransitionEvent(
    productIds: List<String>,
    transition: String,
    streamMachineClient: StreamMachineClient
) = launch {
    withContext(Dispatchers.IO) {
        productIds
            .forEach {
                streamMachineClient.send(
                    createClickstreamEvent(call.request, it, transition),
                    SerializationType.AVRO_BINARY
                ).asDeferred().await()
            }
    }
}

private fun createClickstreamEvent(
    request: ApplicationRequest,
    productId: String,
    transition: String
) = ClickstreamEvent.newBuilder()
    .setAbTests(emptyList())
    .setEventType("product-$productId-$transition")
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

