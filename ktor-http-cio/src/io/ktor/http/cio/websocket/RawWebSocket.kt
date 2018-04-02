package io.ktor.http.cio.websocket

import io.ktor.cio.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.io.*
import kotlinx.io.pool.*
import kotlin.coroutines.experimental.*
import kotlin.properties.*

class RawWebSocket(
    input: ByteReadChannel, output: ByteWriteChannel,
    val serviceDispatcher: CoroutineContext,
    maxFrameSize: Long = Int.MAX_VALUE.toLong(),
    masking: Boolean = false,
    pool: ObjectPool<ByteBuffer> = KtorDefaultPool
) : WebSocketSession {
    private val socketJob = Job()

    override val incoming: ReceiveChannel<Frame> get() = reader.incoming
    override val outgoing: SendChannel<Frame> get() = writer.outgoing

    override var maxFrameSize: Long by Delegates.observable(maxFrameSize) { _, _, newValue ->
        reader.maxFrameSize = newValue
    }

    override var masking: Boolean by Delegates.observable(masking) { _, _, newValue ->
        writer.masking = newValue
    }

    internal val writer =
        @Suppress("DEPRECATION") WebSocketWriter(output, socketJob, serviceDispatcher, masking, pool)

    internal val reader =
        @Suppress("DEPRECATION") WebSocketReader(input, maxFrameSize, socketJob, serviceDispatcher, pool)

    override suspend fun flush() = writer.flush()

    override fun terminate() {
        socketJob.cancel(CancellationException("WebSockedHandler terminated normally"))
    }

    override suspend fun close(cause: Throwable?) {
        flush()
        terminate()
    }
}

suspend fun RawWebSocket.start(handler: suspend WebSocketSession.() -> Unit) {
    handler()
    writer.flush()
    terminate()
}
