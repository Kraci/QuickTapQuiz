package com.kraci.quicktapquiz

import android.annotation.SuppressLint
import android.app.Application
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlin.text.Charsets.UTF_8

class HostConnectionManager {

    interface HostConnectionCallback {
        fun onConnectionInitiated(client: String, connectionInfo: ConnectionInfo)
        fun onConnectionSuccessful(client: String)
        fun onDisconnected(client: String)
        fun onPayloadReceived(client: String, message: String)
    }

    companion object {

        @Volatile
        private var INSTANCE: HostConnectionManager? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var connectionsClient: ConnectionsClient

        private val hostConnectionCallbacks: MutableSet<HostConnectionCallback> = mutableSetOf()

        fun getInstance(application: Application): HostConnectionManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: createClient(application).also { INSTANCE = it }
            }

        private fun createClient(application: Application): HostConnectionManager {
            connectionsClient = Nearby.getConnectionsClient(application.applicationContext)
            return HostConnectionManager()
        }

    }

    fun registerCallback(hostConnectionCallback: HostConnectionCallback) {
        hostConnectionCallbacks.add(hostConnectionCallback)
    }

    fun unregisterCallback(hostConnectionCallback: HostConnectionCallback) {
        hostConnectionCallbacks.remove(hostConnectionCallback)
    }

    fun startAdvertise(hostQuizName: String) {

        connectionsClient.startAdvertising(
            hostQuizName,
            "com.kraci.quicktapquiz",
            object : ConnectionLifecycleCallback() {

                override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                    hostConnectionCallbacks.forEach { callback -> callback.onConnectionInitiated(p0, p1) }
                    connectionsClient.acceptConnection(p0, object : PayloadCallback() {

                        override fun onPayloadReceived(client: String, payload: Payload) {
                            payload.asBytes()?.let {
                                hostConnectionCallbacks.forEach { callback -> callback.onPayloadReceived(client, String(it, UTF_8)) }
                            }
                        }

                        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
                            println("Payload transfer update")
                        }

                    })
                }

                override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                    when (p1.status.statusCode) {
                        ConnectionsStatusCodes.STATUS_OK -> hostConnectionCallbacks.forEach { callback -> callback.onConnectionSuccessful(p0) }
                        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> println("FATAL (need handle) -> STATUS: REJECTED")
                        ConnectionsStatusCodes.STATUS_ERROR -> println("FATAL (need handle) -> STATUS: ERROR")
                    }
                }

                override fun onDisconnected(p0: String) {
                    hostConnectionCallbacks.forEach { callback -> callback.onDisconnected(p0) }
                }

         },
         AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build())
         .addOnSuccessListener {
             println("ADVERTISING!")
         }
         .addOnFailureListener {
             // it.printStackTrace()
             println("ADVERTISE FAILURE: $it")
         }
    }

    fun sendPayload(client: String, payload: Payload) {
        connectionsClient.sendPayload(client, payload)
    }

    fun sendMessage(clients: List<String>, message: String) {
        connectionsClient.sendPayload(clients, Payload.fromBytes(message.toByteArray(UTF_8)))
    }

    fun stopAdvertise() {
        connectionsClient.stopAdvertising()
    }


}