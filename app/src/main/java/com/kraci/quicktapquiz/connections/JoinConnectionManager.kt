package com.kraci.quicktapquiz.connections

import android.annotation.SuppressLint
import android.app.Application
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlin.text.Charsets.UTF_8

class JoinConnectionManager {

    interface JoiningConnectionCallback {
        fun onEndpointFound(host: String, endpointInfo: DiscoveredEndpointInfo)
        fun onEndpointLost(host: String)
    }

    interface JoinConnectionCallback {
        fun onConnectionSuccessful(host: String)
        fun onMessageReceived(host: String, message: String)
        fun onDisconnected(host: String)
    }

    companion object {

        @Volatile
        private var INSTANCE: JoinConnectionManager? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var connectionClient: ConnectionsClient

        private val joiningCallbacks: MutableSet<JoiningConnectionCallback> = mutableSetOf()
        private val joinCallbacks: MutableSet<JoinConnectionCallback> = mutableSetOf()

        fun getInstance(application: Application): JoinConnectionManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: createClient(application).also { INSTANCE = it }
            }

        private fun createClient(application: Application): JoinConnectionManager {
            connectionClient = Nearby.getConnectionsClient(application.applicationContext)
            return JoinConnectionManager()
        }

    }

    var host: String? = null

    fun registerCallback(callback: JoiningConnectionCallback) {
        joiningCallbacks.add(callback)
    }

    fun unregisterCallback(callback: JoiningConnectionCallback) {
        joiningCallbacks.remove(callback)
    }

    fun registerCallback(callback: JoinConnectionCallback) {
        joinCallbacks.add(callback)
    }

    fun unregisterCallback(callback: JoinConnectionCallback) {
        joinCallbacks.remove(callback)
    }

    fun startDiscovery() {
        connectionClient.startDiscovery(
            "com.kraci.quicktapquiz",
            object : EndpointDiscoveryCallback() {

                override fun onEndpointFound(host: String, endpointInfo: DiscoveredEndpointInfo) {
                    joiningCallbacks.forEach { callback -> callback.onEndpointFound(host, endpointInfo) }
                }

                override fun onEndpointLost(host: String) {
                    joiningCallbacks.forEach { callback -> callback.onEndpointLost(host) }
                }

            },
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build())
            .addOnSuccessListener {
                println("DISCOVERY!")
            }
            .addOnFailureListener {
                println("DISCOVERY FAILURE: $it")
            }
    }

    fun stopDiscovery() {
        connectionClient.stopDiscovery()
    }

    fun disconnectFromHost() {
        val hostToDisconnect = host
        if (hostToDisconnect != null) {
            connectionClient.disconnectFromEndpoint(hostToDisconnect)
            host = null
        }
    }

    fun requestConnection(hostID: String, clientName: String) {
        connectionClient.requestConnection(
            clientName,
            hostID,
            object : ConnectionLifecycleCallback() {

                override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                    connectionClient.acceptConnection(p0, object : PayloadCallback() {

                        override fun onPayloadReceived(client: String, payload: Payload) {
                            payload.asBytes()?.let {
                                joinCallbacks.forEach { callback -> callback.onMessageReceived(client, String(it, UTF_8)) }
                            }
                        }

                        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
                            println("Payload transfer update")
                        }

                    })
                }

                override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                    when (p1.status.statusCode) {
                        ConnectionsStatusCodes.STATUS_OK -> joinCallbacks.forEach { callback ->
                            host = p0
                            callback.onConnectionSuccessful(p0)
                        }
                        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> println("FATAL (need handle) -> STATUS: REJECTED")
                        ConnectionsStatusCodes.STATUS_ERROR -> println("FATAL (need handle) -> STATUS: ERROR")
                    }
                }

                override fun onDisconnected(p0: String) {
                    joinCallbacks.forEach { callback -> callback.onDisconnected(p0) }
                }

            }
        )
    }

    fun sendMessageToHost(message: String) {
        val hostToMessage = host
        if (hostToMessage != null) {
            connectionClient.sendPayload(hostToMessage, Payload.fromBytes(message.toByteArray(UTF_8)))
        }
    }

}