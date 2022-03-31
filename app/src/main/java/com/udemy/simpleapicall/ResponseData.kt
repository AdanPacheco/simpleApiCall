package com.udemy.simpleapicall

import com.google.gson.annotations.SerializedName

data class ResponseData(

    @SerializedName(value = "Name")
    val name:String,
    @SerializedName(value = "Dummy_no")
    val dummy_no:Int,
    @SerializedName(value = "Profile_details")
    val profile_details: ProfileDetails,
    val data_list:List<DataListDetail>
)

data class ProfileDetails(
    val is_profile_completed:Boolean,
    val rating:Double
)

data class DataListDetail(
    val id:Int,
    val name:String
)