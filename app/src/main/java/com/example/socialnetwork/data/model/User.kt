package com.example.socialnetwork.data.model

import com.example.socialnetwork.utils.Constants
import com.example.socialnetwork.utils.Constants.DEFAULT_PROFILE_IMAGE
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User (
        val uid: String = "",
        val username : String = "",
        val profilePictureUrl : String = DEFAULT_PROFILE_IMAGE,
        val descriptor : String = "",
        var follows : List<String> = listOf(),
        @get:Exclude
        var isFollowing : Boolean = false
        )