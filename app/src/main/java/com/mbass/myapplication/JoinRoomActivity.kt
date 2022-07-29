package com.mbass.myapplication

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mbass.myapplication.adapter.MessageAdapter
import com.mbass.myapplication.model.GetRoomChatResModel
import com.mbass.myapplication.model.Message
import com.mbass.myapplication.util.ApiInterface
import com.mbass.myapplication.util.Retrofit
import com.mbass.myapplication.util.SharedPrefs
import com.mbass.myapplication.util.SocketManager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_join_room.*
import kotlinx.android.synthetic.main.activity_join_room.lilProgressBar
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinRoomActivity : AppCompatActivity() {
    var mSocket: Socket? = null
    var manager = SocketManager
    var isConnected: Boolean = false
    private var messageList: ArrayList<Message> = ArrayList<Message>()
    private var mUsername: String? = null
    private var receiverName: String? = null
    private var member1: String = ""
    private var member2: String = ""
    private var receiverId: String = ""
    private var roomId: String = ""
    private var mTyping = false
    private val TIMER = 500
    private val REQUEST_CODE = 0
    private var mAdapter: MessageAdapter? = null

    private val typingHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)
        getIntentArgs()
        init()
        initializeSocket()
        getRoomChat()
        listeners()
    }

    private fun getRoomChat() {
        lilProgressBar.visibility = View.VISIBLE
        Retrofit.init(this)
        val apiService: ApiInterface = Retrofit.getRetrofit().create(ApiInterface::class.java)
        val call: Call<GetRoomChatResModel> = apiService.getRoomChat(roomId)
        call.enqueue(object : Callback<GetRoomChatResModel?> {
            override fun onResponse(
                call: Call<GetRoomChatResModel?>,
                response: Response<GetRoomChatResModel?>
            ) {
                val statusCode = response.code()
                if (statusCode == 200 && response.body() != null && response.body()?.chats != null) {
                    for (i in 0 until response.body()?.chats?.size!!) {
                        if (response.body()?.chats?.get(i)?.senderId?.id.equals(
                                SharedPrefs.getUserId(
                                    this@JoinRoomActivity
                                )
                            )
                        ) {
                            response.body()?.chats?.get(i)?.message?.let {
                                response.body()?.chats?.get(i)?.senderId?.name?.let { it1 ->
                                    addMessage(
                                        it1,
                                        it, Message.TYPE_MESSAGE_SENT
                                    )
                                }
                            }
                        } else {
                            response.body()!!.chats?.get(i)?.message?.let {
                                response.body()!!.chats?.get(i)?.receiverId?.name?.let { it1 ->
                                    addMessage(
                                        it1,
                                        it, Message.TYPE_MESSAGE_RECEIVED
                                    )
                                }
                            }
                        }
                    }
                    mAdapter?.notifyDataSetChanged()
                }
                lilProgressBar.visibility = View.GONE
                swipeToRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<GetRoomChatResModel?>, t: Throwable) {
                lilProgressBar.visibility = View.GONE
                swipeToRefresh.isRefreshing = false
            }
        })
    }

    private fun listeners() {
        swipeToRefresh.setOnRefreshListener {
            getRoomChat()
        }
        editMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (mUsername == null) return
                if (!mSocket?.connected()!!) return
                if (TextUtils.isEmpty(editMessage.text)) return
                if (!mTyping) {
                    mTyping = true
                    mSocket?.emit("typing-start")
                }
                typingHandler.removeCallbacks(onTypingTimeout)
                typingHandler.postDelayed(onTypingTimeout, TIMER.toLong())
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
        sendButton.setOnClickListener { view: View? -> attemptSend() }
    }

    private fun getIntentArgs() {
        mUsername = intent.getStringExtra("username")
        receiverId = intent.getStringExtra("receiverId").toString()
        roomId = intent.getStringExtra("roomId").toString()
        receiverName = intent.getStringExtra("receiverName").toString()
    }

    private fun init() {
        messageList = java.util.ArrayList()
        mAdapter = MessageAdapter(messageList)
        recycler_view.adapter = mAdapter
    }

    private fun initializeSocket() {
        SocketManager.setSocket()
        SocketManager.establishConnection()
        mSocket = SocketManager.getSocket()
        mSocket?.emit("join-room", roomId, SharedPrefs.getUserId(this))
        mSocket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket?.on("user-connected", userConnected)
        mSocket?.on("user left", onUserLeft)
        mSocket?.on("new-message", onNewMessage)
        mSocket?.on("user-typing-start", onTyping)
        mSocket?.on("user-typing-stop", onStopTyping)
    }

    private val onDisconnect = Emitter.Listener {
        runOnUiThread {
            isConnected = false
            Log.w("TAG", "onDisconnect")
        }
    }

    private val onConnectError =
        Emitter.Listener { args: Array<Any?>? ->
            runOnUiThread {
                Log.e(
                    "TAG",
                    "error connecting"
                )
            }
        }

    private val userConnected =
        Emitter.Listener { args: Array<Any?>? ->
            runOnUiThread {
                Log.e(
                    "TAG",
                    "User connected"
                )
            }
        }

    private val onNewMessage = Emitter.Listener { args: Array<Any> ->
        Log.w("TAG", "onNewMesage" + args)
        runOnUiThread {
            try {
                val data = args[0] as JSONObject
                if (!data.get("senderId").equals(SharedPrefs.getUserId(this@JoinRoomActivity))) {
                    var username: String? = null
                    var message: String? = null
                    username = mUsername
                    message = data.getString("message")

                    username?.let {
                        message?.let { it1 ->
                            addMessage(
                                it,
                                it1, Message.TYPE_MESSAGE_RECEIVED
                            )
                        }
                    }
                }
            } catch (e: JSONException) {
                Log.e("TAG", e.message!!)
                e.printStackTrace()
            }
        }
    }

    private val onUserJoined = Emitter.Listener { args: Array<Any> ->
        Log.w("TAG", "onUserJoined")
        runOnUiThread {
            val data = args[0] as JSONObject
            var username: String? = null
            val numUsers: Int
            try {
                username = data.getString("username")
                numUsers = data.getInt("numUsers")
            } catch (e: JSONException) {
                Log.e("TAG", e.message!!)
                return@runOnUiThread
            }
            addLog("$username has joined")
            addParticipantsLog(numUsers)
        }
    }

    private val onUserLeft = Emitter.Listener { args: Array<Any> ->
        Log.w("TAG", "onUserLeft")
        runOnUiThread {
            val data = args[0] as JSONObject
            val username: String
            val numUsers: Int
            try {
                username = data.getString("username")
                numUsers = data.getInt("numUsers")
            } catch (e: JSONException) {
                Log.e("TAG", e.message!!)
                return@runOnUiThread
            }
            addLog("$username left")
            addParticipantsLog(numUsers)
            removeTyping()
        }
    }

    private val onTyping = Emitter.Listener { args: Array<Any> ->
        Log.w("TAG", "onTyping")
        runOnUiThread {
            //  val data = args[0] as JSONObject
            // Log.d("TAG", ": onTyping" + data)
            if (args[0] != SharedPrefs.getUserId(this)) {
                val username: String = receiverName.toString()
                addTyping(username)
            }
        }
    }

    private val onStopTyping = Emitter.Listener { args: Array<Any?>? ->
        Log.w("TAG", "onStopTyping")
        // val data = args?.get(0) as JSONObject
        //Log.d("TAG", ": onStopTyping" + data)

        runOnUiThread { this.removeTyping() }
    }

    private fun addLog(message: String) {
        messageList.add(
            Message(
                Message.TYPE_LOG,
                message
            )
        )
        mAdapter?.notifyItemInserted(messageList?.size - 1)
        scrollUp()
    }

    private fun addMessage(username: String, message: String, messageType: Int) {
        messageList.add(
            Message(
                messageType,
                username,
                message
            )
        )
        mAdapter?.notifyDataSetChanged()
        scrollUp()
    }

    private fun addParticipantsLog(numUsers: Int) {
        addLog("There are $numUsers users in the chat room")
        scrollUp()
    }

    private fun addTyping(username: String) {
        typing.text = username.trim { it <= ' ' } + " is typing"
    }

    private fun removeTyping() {
        typing.text = null
    }

    private fun attemptSend() {
        if (mUsername == null) return
        if (!mSocket?.connected()!!) return
        if (mTyping) {
            mTyping = false
            mSocket?.emit("typing-stop")
        }
        val message: String = editMessage.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(message)) {
            editMessage.requestFocus()
            return
        }
        editMessage.setText("")
        addMessage(mUsername!!, message, Message.TYPE_MESSAGE_SENT)

        // perform the sending message attempt.
        mSocket?.emit("message", SharedPrefs.getUserId(this), receiverId, message)
    }

    private val onTypingTimeout = Runnable {
        if (!mTyping) return@Runnable
        mTyping = false
        mSocket?.emit("typing-stop")
    }

    private fun scrollUp() {
        mAdapter?.itemCount?.minus(1)?.let { recycler_view.scrollToPosition(it) }
    }
}