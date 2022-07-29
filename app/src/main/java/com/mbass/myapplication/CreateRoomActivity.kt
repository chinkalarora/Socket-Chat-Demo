package com.mbass.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import com.mbass.myapplication.adapter.CreateRoomAdapter
import com.mbass.myapplication.callbacks.UserCallbacks
import com.mbass.myapplication.model.CreateRoomResModel
import com.mbass.myapplication.model.GetUsersListModel
import com.mbass.myapplication.util.ApiInterface
import com.mbass.myapplication.util.Retrofit
import com.mbass.myapplication.util.SharedPrefs
import kotlinx.android.synthetic.main.activity_create_room.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateRoomActivity : AppCompatActivity(), UserCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)
        getUsersList()

        listeners()
    }

    private fun listeners() {
        swipeToRefresh.setOnRefreshListener {getUsersList()  }
    }

    var usersList = ArrayList<GetUsersListModel.User>()
    private fun getUsersList() {
        lilProgressBar.visibility = View.VISIBLE
        Retrofit.init(this)
        val apiService: ApiInterface = Retrofit.getRetrofit().create(ApiInterface::class.java)
        val call: Call<GetUsersListModel> = apiService.usersList
        call.enqueue(object : Callback<GetUsersListModel?> {
            override fun onResponse(
                call: Call<GetUsersListModel?>,
                response: Response<GetUsersListModel?>
            ) {
                val statusCode = response.code()
                if (statusCode == 200 && response.body() != null) {
                    usersList = response.body()?.users as ArrayList<GetUsersListModel.User>
                    val adapter = CreateRoomAdapter(
                        usersList,
                        this@CreateRoomActivity,
                        this@CreateRoomActivity
                    )
                    rcvUsers.adapter = adapter
                }
                lilProgressBar.visibility = View.GONE
                swipeToRefresh.isRefreshing=false
            }

            override fun onFailure(call: Call<GetUsersListModel?>, t: Throwable) {
                lilProgressBar.visibility = View.GONE
                swipeToRefresh.isRefreshing=false
            }
        })
    }

    private fun createRoom(member1: String, member2: String, name: String, receiverId: String) {
        lilProgressBar.visibility = View.VISIBLE
        Retrofit.init(this)
        val loginParams = HashMap<String, String>()
        loginParams["member1"] = member1
        loginParams["member2"] = member2
        val apiService: ApiInterface = Retrofit.getRetrofit().create(ApiInterface::class.java)
        val call: Call<CreateRoomResModel> = apiService.createRoom(loginParams)
        call.enqueue(object : Callback<CreateRoomResModel?> {
            override fun onResponse(
                call: Call<CreateRoomResModel?>,
                response: Response<CreateRoomResModel?>
            ) {
                val statusCode = response.code()
                if (statusCode == 200 && response.body() != null) {
                    val intent = Intent(this@CreateRoomActivity, JoinRoomActivity::class.java)
                    intent.putExtra(
                        "username",
                        SharedPrefs.getLoginDetail(this@CreateRoomActivity)?.user?.name
                    )
                    intent.putExtra("roomId", response.body()!!.data?.room?.id)
                    intent.putExtra("receiverName", name)





                    intent.putExtra("receiverId", receiverId)
                    startActivity(intent)
                }
                lilProgressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<CreateRoomResModel?>, t: Throwable) {
                lilProgressBar.visibility = View.GONE
            }
        })
    }


    override fun onUserClick(room: GetUsersListModel.User,name: String, receiverId: String) {
        room.id?.let { SharedPrefs.getUserId(this@CreateRoomActivity)
            ?.let { it1 -> createRoom(it1, it,name,receiverId) } }
    }
}