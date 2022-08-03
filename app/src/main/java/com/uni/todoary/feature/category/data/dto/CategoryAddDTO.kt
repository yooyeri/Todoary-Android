package com.uni.todoary.feature.category.data.dto

import com.google.gson.annotations.SerializedName

data class CategoryAddRequest(
    @SerializedName("title") val title: String,
    @SerializedName("color") val color : Int
)
