package io.ktor.http.cio.websocket

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*
import java.time.*
import java.util.concurrent.*
import kotlin.coroutines.experimental.*

///**
// * Starts websocket close sequence actor job on [coroutineContext] for websocket [session]
// * A close sequence job is listening for close events. If a client initiates close then the job is replying to client's
// * close and quits. If the [session] is terminating (e.g. the server is going down) then the job is sending close frame
// * and waiting for client's reply (up to timeout provided by [timeout] function) and then quits.
// *
// * Just before do quit the job calls [populateCloseReason] with last known close reason however it may
// * provide `null` reason it get cancelled.
// *
// * Once the job is completed, the connection could be terminated.
// */
//fun closeSequence(
//    coroutineContext: CoroutineContext,
//    session: WebSocketSession,
//    timeout: Duration,
//    populateCloseReason: (reason: CloseReason?) -> Unit
//): SendChannel<CloseFrameEvent> = actor(coroutineContext, capacity = 2, start = CoroutineStart.LAZY) {
//    println("close sequence started")
//    var reason: CloseReason? = null
//
//    try {
//        val firstCloseEvent = receiveOrNull() ?: return@actor
//
//        withTimeoutOrNull(timeout.toMillis(), TimeUnit.MILLISECONDS) {
//            reason = firstCloseEvent.frame.readReason()
//            when (firstCloseEvent) {
//                is CloseFrameEvent.ToSend -> {
//                    session.send(firstCloseEvent.frame)
//
//                    while (true) {
//                        receiveOrNull() as? CloseFrameEvent.ToSend ?: break
//                    }
//                }
//
//                is CloseFrameEvent.Received -> {
//                    session.send(Frame.Close(reason ?: CloseReason(CloseReason.Codes.NORMAL, "OK")))
//                    session.flush()
//                }
//            }
//        }
//    } finally {
//        // terminate connection in any case
//        populateCloseReason(reason)
//        println("close sequence done")
//    }
//}

//suspend fun WebSocketSession.sendCloseFrame(
//    frame: Frame.Close = Frame.Close(CloseReason(CloseReason.Codes.NORMAL, "OK")),
//    clientReceived: Boolean = false
//) {
//    send(frame)
//    flush()
//}
