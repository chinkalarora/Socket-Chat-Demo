package com.mbass.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.mbass.myapplication.R
import com.mbass.myapplication.callbacks.UserCallbacks
import com.mbass.myapplication.model.GetUsersListModel

class CreateRoomAdapter(
    var list: List<GetUsersListModel.User>,
    var context: Context,
    var onUserCallback: UserCallbacks
) :
    RecyclerView.Adapter<CreateRoomAdapter.HolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderClass {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_join_room, parent, false)
        return HolderClass(v)
    }

    override fun onBindViewHolder(holder: HolderClass, position: Int) {
        holder.bindItems(context, list[position], onUserCallback)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class HolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var txtRoomName: AppCompatTextView
        lateinit var txtRoomEmail: AppCompatTextView
        fun bindItems(
            context: Context,
            room: GetUsersListModel.User,
            onUserCallback: UserCallbacks
        ) {
            var reciverID: String = ""
            var receiverName: String = ""
            txtRoomName = itemView.findViewById(R.id.txtRoomName)
            txtRoomEmail = itemView.findViewById(R.id.txtRoomEmail)

            txtRoomName.text = room.name
            txtRoomEmail.text = room.email
            txtRoomName.setOnClickListener {
                room.id?.let { it1 ->
                    room.name?.let { it2 ->
                        onUserCallback.onUserClick(
                            room,
                            it2, it1
                        )
                    }
                }
            }
        }
    }
}