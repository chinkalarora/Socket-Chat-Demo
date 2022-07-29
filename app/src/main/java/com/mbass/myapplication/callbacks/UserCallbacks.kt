package com.mbass.myapplication.callbacks

import com.mbass.myapplication.model.GetUsersListModel

interface UserCallbacks {
    fun onUserClick(room: GetUsersListModel.User,name: String, receiverId: String)
}