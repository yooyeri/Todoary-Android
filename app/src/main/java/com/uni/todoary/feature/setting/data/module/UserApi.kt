package com.uni.todoary.feature.setting.data.module

import com.google.gson.annotations.SerializedName
import com.uni.todoary.base.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

data class ProfileChangeRequest(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("introduce") val introduce : String
)

data class AlarmStatus(
    @SerializedName("userId") val userId: Int,
    @SerializedName("isTodoAlarmChecked") val isTodoAlarmChecked : Boolean,
    @SerializedName("isDiaryAlarmChecked") val isDiaryAlarmChecked: Boolean,
    @SerializedName("isRemindAlarmChecked") val isRemindAlarmChecked : Boolean
)

data class AlarmUpdateRequest(
    @SerializedName("isChecked") val isChecked : Boolean
)

interface UserInterface {
    @PATCH("/users/profile")
    suspend fun changeProfile(@Body ChangeProfileInfo : ProfileChangeRequest) : Response<BaseResponse<ProfileChangeRequest>>

    @PATCH("/users/status")
    suspend fun deleteUser() : Response<BaseResponse<Any>>

    @POST("/users/signout")
    suspend fun logOut() : Response<BaseResponse<Any>>

    // -------------- alarm update --------------- //
    @GET("/users/alarm")
    suspend fun getAlarmStatus() : Response<BaseResponse<AlarmStatus>>

    @PATCH("/users/alarm/todo")
    suspend fun updateAlarmTodo(@Body alarmRequest : AlarmUpdateRequest) : Response<BaseResponse<Any>>

    @PATCH("/users/alarm/diary")
    suspend fun updateAlarmDiary(@Body alarmRequest : AlarmUpdateRequest) : Response<BaseResponse<Any>>

    @PATCH("/users/alarm/remind")
    suspend fun updateAlarmRemind(@Body alarmRequest : AlarmUpdateRequest) : Response<BaseResponse<Any>>
}