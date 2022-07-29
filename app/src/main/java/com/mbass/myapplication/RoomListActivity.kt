package com.mbass.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mbass.myapplication.adapter.JoinRoomAdapter
import com.mbass.myapplication.model.GetRoomListModel
import com.mbass.myapplication.util.ApiInterface
import com.mbass.myapplication.util.Retrofit
import com.mbass.myapplication.util.SharedPrefs
import kotlinx.android.synthetic.main.activity_room_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomListActivity : AppCompatActivity() {

    var authorization: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        Retrofit.init(this)
        authorization = intent.getStringExtra("authorization").toString()
        getRoomDetails()

        listeners()

    }

    private fun listeners() {
        swipeToRefresh.setOnRefreshListener { getRoomDetails() }
        txtLogout.setOnClickListener {
            SharedPrefs.storeToken(this, "")
            val intent = Intent(this@RoomListActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        txtCreateRoom.setOnClickListener {
            val intent = Intent(this@RoomListActivity, CreateRoomActivity::class.java)
            startActivity(intent)
        }
    }


    var list: List<GetRoomListModel.Room> = ArrayList<GetRoomListModel.Room>()
    private fun getRoomDetails() {
        lilProgressBar.visibility = View.VISIBLE
        val apiService: ApiInterface = Retrofit.getRetrofit().create(ApiInterface::class.java)
        val call: Call<GetRoomListModel> = apiService.roomsList
        call.enqueue(object : Callback<GetRoomListModel?> {
            override fun onResponse(
                call: Call<GetRoomListModel?>,
                response: Response<GetRoomListModel?>
            ) {
                val statusCode = response.code()
                if (statusCode == 200) {
                    list = response.body()?.rooms!!
                    var adapter = JoinRoomAdapter(list, this@RoomListActivity)
                    rcvListOfRoom.adapter = adapter
                }
                lilProgressBar.visibility = View.GONE
                swipeToRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<GetRoomListModel?>, t: Throwable) {
                lilProgressBar.visibility = View.GONE
                swipeToRefresh.isRefreshing = false

            }
        })
    }
}