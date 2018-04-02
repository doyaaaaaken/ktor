package io.ktor.client.features.websocket

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.experimental.*

suspend fun HttpClient.websocketRaw(
    method: HttpMethod = HttpMethod.Get, host: String = "localhost", port: Int = 80, path: String = "/",
    block: HttpRequestBuilder.() -> Unit = {}
): WebSocketSession = request {
    this.method = method
    url("ws", host, port, path)
    block()
}

suspend fun HttpClient.websocket(
    method: HttpMethod = HttpMethod.Get, host: String = "localhost", port: Int = 80, path: String = "/",
    block: HttpRequestBuilder.() -> Unit = {}
): DefaultWebSocketSession {
    val session = websocketRaw(method, host, port, path, block)
    return DefaultWebSocketSessionImpl(session, Job())
}

suspend fun HttpClient.wsRaw(
    method: HttpMethod = HttpMethod.Get, host: String = "localhost", port: Int = 80, path: String = "/",
    request: HttpRequestBuilder.() -> Unit = {}, block: suspend WebSocketSession.() -> Unit
): Unit = websocketRaw(method, host, port, path, {
    url.protocol = URLProtocol.WS
    request()
}).block()

suspend fun HttpClient.wssRaw(
    method: HttpMethod = HttpMethod.Get, host: String = "localhost", port: Int = 80, path: String = "/",
    request: HttpRequestBuilder.() -> Unit = {}, block: suspend WebSocketSession.() -> Unit
): Unit = websocketRaw(method, host, port, path, {
    url.protocol = URLProtocol.WSS
    request()
}).block()

suspend fun HttpClient.ws(
    method: HttpMethod = HttpMethod.Get, host: String = "localhost", port: Int = 80, path: String = "/",
    request: HttpRequestBuilder.() -> Unit = {}, block: suspend WebSocketSession.() -> Unit
): Unit = websocket(method, host, port, path, {
    url.protocol = URLProtocol.WS
    request()
}).block()

suspend fun HttpClient.wss(
    method: HttpMethod = HttpMethod.Get, host: String = "localhost", port: Int = 80, path: String = "/",
    request: HttpRequestBuilder.() -> Unit = {}, block: suspend WebSocketSession.() -> Unit
): Unit = websocket(method, host, port, path, {
    url.protocol = URLProtocol.WSS
    request()
}).block()
