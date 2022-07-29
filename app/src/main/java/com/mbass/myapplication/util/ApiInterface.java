package com.mbass.myapplication.util;



import com.mbass.myapplication.model.CreateRoomResModel;
import com.mbass.myapplication.model.GetRoomChatResModel;
import com.mbass.myapplication.model.GetRoomListModel;
import com.mbass.myapplication.model.LoginAPIResposne;
import com.mbass.myapplication.model.GetUsersListModel;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST(AppConfig.LOGIN_API)
    Call<LoginAPIResposne> loginAPICall(@Body HashMap<String, String> loginDetails);

    @POST(AppConfig.CREATE_ROOMS)
    Call<CreateRoomResModel> createRoom(@Body HashMap<String, String> roomDetails);

    @GET(AppConfig.GET_ROOMS)
    Call<GetRoomListModel> getRoomsList();

    @GET(AppConfig.GET_USERS_LIST)
    Call<GetUsersListModel> getUsersList();

    @GET(AppConfig.GET_ROOM_CHATS)
    Call<GetRoomChatResModel> getRoomChat(@Query("roomId") String roomId );

}
















