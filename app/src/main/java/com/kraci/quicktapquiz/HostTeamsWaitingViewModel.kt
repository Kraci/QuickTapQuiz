package com.kraci.quicktapquiz

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class HostTeamsWaitingViewModel(application: Application): AndroidViewModel(application), LifecycleObserver {

    private val _teamsJoined: MutableLiveData<List<Int>> = MutableLiveData()
    private val _startQuizButton: LiveEvent<Any> = LiveEvent()
    private val _application: Application = application

    var hostQuizName = "HOST"

    init {
        _teamsJoined.value = listOf(1 , 2, 3)
    }

    val startQuizButton: LiveData<Any>
        get() = _startQuizButton

    val teamsJoined: LiveData<List<Int>>
        get() = _teamsJoined

    fun startQuizTapped() {
        _startQuizButton.call()
    }

    // NETWORKING

    val payloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(p0: String, p1: Payload) {
            println("Payload received")
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            println("Payload transfer update")
        }

    }

    val advertisingOption = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
    val connectionCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
            println("CONNECTION CALLBACK: INITIATED")
            Nearby.getConnectionsClient(_application.applicationContext)
                .acceptConnection(p0, payloadCallback)
        }

        override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
            println("CONNECTION CALLBACK: RESULT p0 -> $p1")
            when (p1.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> println("STATUS: OK")
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> println("STATUS: REJECTED")
                ConnectionsStatusCodes.STATUS_ERROR -> println("STATUS: ERROR")
            }
        }

        override fun onDisconnected(p0: String) {
            println("CONNECTION CALLBACK: DISCONNECT")
        }

    }

    fun startAdvertise() {

        Nearby.getConnectionsClient(_application.applicationContext)
            .startAdvertising(
                hostQuizName,
                "com.kraci.quicktapquiz",
                connectionCallback,
                advertisingOption
            )
            .addOnSuccessListener {
                println("ADVERTISING!")
            }
            .addOnFailureListener {
                println("ADVERTISE FAILURE: $it")
            }

    }

}