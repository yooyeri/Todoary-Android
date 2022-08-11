package com.uni.todoary.feature.category.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryAddRequest(
    @SerializedName("title") val title: String,
    @SerializedName("color") val color : Int
) : Parcelable
