package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.kraci.quicktapquiz.databinding.ActivityJoinQuizChooseBinding

class JoinQuizChooseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinQuizChooseBinding
    private lateinit var joinQuizChooseViewModel: JoinQuizChooseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Choose a Game"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_quiz_choose)

        joinQuizChooseViewModel = ViewModelProviders.of(this).get(JoinQuizChooseViewModel::class.java).apply {
            hostedGames.observe(this@JoinQuizChooseActivity, Observer {
                println("FOUND: $it")
                adapter.hostedGames = it
            })
            hostGamePicked.observe(this@JoinQuizChooseActivity, Observer {
                // create new activity :-)
                println("TAPPED: $it")
            })
            emptyNameEvent.observe(this@JoinQuizChooseActivity, Observer {
                Toast.makeText(baseContext, "You must enter a team name.", Toast.LENGTH_LONG).show()
            })
        }

        binding.hostedGames.layoutManager = LinearLayoutManager(baseContext)
        binding.hostedGames.setHasFixedSize(true)
        binding.viewModel = joinQuizChooseViewModel

        assert(binding.viewModel == null) {
            println("VIEW MODEL IS NULL, CAN'T DISCOVER!")
        }
        binding.viewModel?.startDiscovery()

        lifecycle.addObserver(joinQuizChooseViewModel)

        // NETWORKING AND BUTTON

//        val hostedGame = findViewById<Button>(R.id.hosted_game)

        // String ID pre HOST, toto bude potom pole vo VM a adapteri
        var hostID = ""

        val discoveryOption = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        val discoveryCallback = object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(p0: String, p1: DiscoveredEndpointInfo) {
                // tu vytvarat pole najdenych zariadeni a pridavat do recycler view
                hostID = p0
//                hostedGame.text = p1.endpointName
                println("ENDPOINT FOUND: $p0")
            }

            override fun onEndpointLost(p0: String) {
                println("Endpoint Lost")
            }

        }

//        Nearby.getConnectionsClient(applicationContext)
//            .startDiscovery(
//                "com.kraci.quicktapquiz",
//                discoveryCallback,
//                discoveryOption
//            )
//            .addOnSuccessListener {
//                println("DISCOVERING")
//            }
//            .addOnFailureListener {
//                println("DISCOVERY FAILED: $it")
//            }


//        hostedGame.setOnClickListener {
//
//            val payloadCallback = object : PayloadCallback() {
//
//                override fun onPayloadReceived(p0: String, p1: Payload) {
//                    //To change body of created functions use File | Settings | File Templates.
//                }
//
//                override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
//                    //To change body of created functions use File | Settings | File Templates.
//                }
//
//            }
//
//            val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
//
//                override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
//                    Nearby.getConnectionsClient(applicationContext).acceptConnection(p0, payloadCallback)
//                }
//
//                override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
//                    println("CONNECTION CALLBACK: RESULT p0 -> $p0")
//                    when (p1.status.statusCode) {
//                        ConnectionsStatusCodes.STATUS_OK -> println("STATUS: OK")
//                        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> println("STATUS: REJECTED")
//                        ConnectionsStatusCodes.STATUS_ERROR -> println("STATUS: ERROR")
//                    }
//                }
//
//                override fun onDisconnected(p0: String) {
//                    println("STATUS: onDisconnected")
//                }
//
//            }
//
//            val teamName = findViewById<EditText>(R.id.team_name)
//
//            Nearby.getConnectionsClient(applicationContext)
//                .requestConnection(
//                    teamName.text.toString(),
//                    hostID,
//                    connectionLifecycleCallback
//                )
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(joinQuizChooseViewModel)
    }
}
