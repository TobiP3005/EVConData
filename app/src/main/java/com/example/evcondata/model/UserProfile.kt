package com.example.evcondata.model

data class UserProfile(
    var publishConsumption: Boolean,
    var myCar: String,
    var owner: String,
    val type: String = "userprofile"
)