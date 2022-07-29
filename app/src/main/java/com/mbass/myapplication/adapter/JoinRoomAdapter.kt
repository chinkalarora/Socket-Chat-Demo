package com.mbass.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.mbass.myapplication.JoinRoomActivity
import com.mbass.myapplication.R
import com.mbass.myapplication.model.GetRoomListModel
import com.mbass.myapplication.util.SharedPrefs

class JoinRoomAdapter(var list: List<GetRoomListModel.Room>, var context: Context) :
    RecyclerView.Adapter<JoinRoomAdapter.HolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderClass {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_join_room, parent, false)
        return HolderClass(v)
    }

    override fun onBindViewHolder(holder: HolderClass, position: Int) {
        holder.bindItems(context, list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class HolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var txtRoomName: AppCompatTextView
        lateinit var txtRoomEmail: AppCompatTextView
        fun bindItems(
            context: Context,
            room: GetRoomListModel.Room
        ) {
            var reciverID: String = ""
            var receiverName: String = ""
            txtRoomName = itemView.findViewById(R.id.txtRoomName)
            txtRoomEmail = itemView.findViewById(R.id.txtRoomEmail)
            if (!room.member1Id?.id?.toString().equals(SharedPrefs.getUserId(context))) {
                txtRoomName.text = room.member1Id?.name
                receiverName = room.member1Id?.name.toString()
                txtRoomEmail.text = room.member1Id?.email
            } else if (!room.member2Id?.id?.toString().equals(SharedPrefs.getUserId(context))) {
                txtRoomName.text = room.member2Id?.name
                receiverName = room.member2Id?.name.toString()
                txtRoomEmail.text = room.member2Id?.email
            }

            txtRoomName.setOnClickListener {
                val intent = Intent(context, JoinRoomActivity::class.java)
                intent.putExtra("username", SharedPrefs.getLoginDetail(context)?.user?.name)
                intent.putExtra("roomId", room.id)
                intent.putExtra("receiverName", receiverName)
                if (!room.member1Id?.id?.toString().equals(SharedPrefs.getUserId(context))) {
                    intent.putExtra("receiverId", room.member1Id?.id)
                } else if (!room.member2Id?.id?.toString().equals(SharedPrefs.getUserId(context))) {
                    intent.putExtra("receiverId", room.member2Id?.id)
                }
                context.startActivity(intent)
            }
        }
    }
}