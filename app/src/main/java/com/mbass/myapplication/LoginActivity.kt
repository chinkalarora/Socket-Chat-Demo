package com.mbass.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mbass.myapplication.model.LoginAPIResposne
import com.mbass.myapplication.util.ApiInterface
import com.mbass.myapplication.util.Retrofit
import com.mbass.myapplication.util.SharedPrefs
import com.mbass.myapplication.util.SocketManager
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var mSocket: Socket? = null
    var responseLogin = LoginAPIResposne()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SocketManager.setSocket()
        SocketManager.establishConnection()
        mSocket = SocketManager.getSocket()
        mSocket!!.emit("login", JSONObject().apply {
            put("email", "chinkal@openxcell.info")
            put("password", "Admin@123")
        })

        btnLogin.setOnClickListener {
            if (edtUserName.text.isNullOrEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter Email/ Username",
                    Toast.LENGTH_LONG
                ).show()
            } else if (edtPassword.text.isNullOrEmpty()) {
                Toast.makeText(this@LoginActivity, "Please enter Password", Toast.LENGTH_LONG)
                    .show()
            } else {
                getLoginCredentials()
            }
        }
    }

    private fun getLoginCredentials() {
        lilProgressBar.visibility = View.VISIBLE
        Retrofit.init(this)
        val loginParams = HashMap<String, String>()
        loginParams["email"] = edtUserName.text?.trim().toString()
        loginParams["password"] = edtPassword.text?.trim().toString()

        val apiService: ApiInterface = Retrofit.getRetrofit().create(ApiInterface::class.java)
        val call: Call<LoginAPIResposne> = apiService.loginAPICall(loginParams)
        call.enqueue(object : Callback<LoginAPIResposne?> {
            override fun onResponse(
                call: Call<LoginAPIResposne?>,
                response: Response<LoginAPIResposne?>
            ) {
                val statusCode = response.code()

                if (statusCode == 200) {
                    responseLogin = response.body()!!
                    Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginActivity, RoomListActivity::class.java)
                    intent.putExtra("authorization", responseLogin.token)
                    SharedPrefs.storeToken(this@LoginActivity, responseLogin.token.toString())
                    SharedPrefs.storeUserId(this@LoginActivity, responseLogin.userId.toString())
                    SharedPrefs.storeLoginDetail(this@LoginActivity, responseLogin)
                    startActivity(intent)
                    finish()
                } else if (statusCode == 400) {
                    Toast.makeText(this@LoginActivity, "Wrong Credentials", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_LONG).show()
                }
                lilProgressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<LoginAPIResposne?>, t: Throwable) {
                lilProgressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Failure", Toast.LENGTH_LONG).show();
            }
        })
    }
}