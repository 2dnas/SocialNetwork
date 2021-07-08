package com.example.socialnetwork.data.model

import android.net.Uri

data class ProfileUpdate (
    val uidToProfile : String = "",
    val username : String = "",
    val description : String = "",
    val profilePictureUrl : Uri? = null
        )