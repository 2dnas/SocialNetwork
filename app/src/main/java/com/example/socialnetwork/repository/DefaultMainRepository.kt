package com.example.socialnetwork.repository

import android.net.Uri
import com.example.socialnetwork.data.model.*
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.other.safeCall
import com.example.socialnetwork.utils.Constants.DEFAULT_PROFILE_IMAGE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    private val auth : FirebaseAuth,
    private val storage : FirebaseStorage,
    private val firestore : FirebaseFirestore,
    private val realtime : FirebaseDatabase
) {
    private val usersRef = firestore.collection("users")
    private val postsRef = firestore.collection("posts")
    private val commentsRef = firestore.collection("comments")

    private val chatMessages = mutableListOf<Message>()


    suspend fun createPost(uri : Uri, text:String) : UiState<Any> = withContext(Dispatchers.IO){
        safeCall {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            val imageUploadResult = storage.getReference(postId).putFile(uri).await()
            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            val post = Post(
                id = postId,
                authorId = uid,
                text = text,
                imageUrl = imageUrl,
                date = System.currentTimeMillis()
            )
            postsRef.document(postId).set(post).await()
            UiState.Success(Any())
        }
    }

    suspend fun deletePost(post : Post) = withContext(Dispatchers.IO){
        safeCall {
            postsRef.document(post.id).delete().await()
            storage.getReferenceFromUrl(post.imageUrl).delete().await()
            UiState.Success(post)
        }
    }

    suspend fun getUsers(uids : List<String>) = withContext(Dispatchers.IO) {
        safeCall {
            val usersList = usersRef.whereIn("uid",uids).get().await().toObjects(User::class.java)
            UiState.Success(usersList)
        }
    }

    suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = usersRef.document(uid).get().await().toObject(User::class.java)
            val currentUid = auth.uid!!
            val currentUser = usersRef.document(currentUid).get().await().toObject(User::class.java)!!
            user?.isFollowing = uid in currentUser.follows
            UiState.Success(user)
        }
    }

    suspend fun searchUser(query : String)  = withContext(Dispatchers.IO) {
        safeCall {
            val userResult = usersRef.whereGreaterThanOrEqualTo("username",
                query.uppercase(Locale.ROOT)
            )
                .get().await().toObjects(User::class.java)
            UiState.Success(userResult)
        }
    }


    suspend fun getPostsFromFollows() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val follows = getUser(uid).data!!.follows
            val posts = postsRef.whereIn("authorId",follows).get().await().toObjects(Post::class.java)
                .onEach { post ->
                    val user = getUser(post.authorId).data!!
                    post.authorProfilePictureUrl = user.profilePictureUrl
                    post.authorUsername = user.username
                    post.isLiked = uid in post.likedBy
                }
            UiState.Success(posts)
        }
    }

    suspend fun toggleFollowForUser(uid : String) : UiState<Boolean> = withContext(Dispatchers.IO) {
        safeCall {
            var isFollowing = false
            firestore.runTransaction { transaction ->
                val currentUid = auth.uid!!
                val currentUser = transaction.get(usersRef.document(currentUid)).toObject(User::class.java)!!
                isFollowing = uid in currentUser.follows
                val newFollows : List<String> = if(isFollowing) currentUser.follows - uid else currentUser.follows + uid
                transaction.update(usersRef.document(currentUid),"follows",newFollows)
            }.await()
            UiState.Success(!isFollowing)
        }
    }

    suspend fun getPostsForProfile(uid :String) : UiState<List<Post>> = withContext(Dispatchers.IO) {
        safeCall {
            val profilePosts = postsRef.whereEqualTo("authorId",uid)
                .orderBy("date",Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
                .onEach { post ->
                    val user = getUser(post.authorId).data!!
                    post.authorProfilePictureUrl = user.profilePictureUrl
                    post.authorUsername = user.username
                    post.isLiked = uid in post.likedBy
                }
            UiState.Success(profilePosts)
        }
    }



    suspend fun toggleLikeForPosts(post: Post) = withContext(Dispatchers.IO){
        safeCall {
            var isLiked = false
            firestore.runTransaction { transaction ->
                val uid = auth.uid!!
                val postResult = transaction.get(postsRef.document(post.id))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                transaction.update(
                    postsRef.document(post.id),
                    "likedBy",
                    if(uid in currentLikes) {
                        currentLikes - uid
                    } else {
                        isLiked = true
                        currentLikes + uid
                    }
                )
            }.await()
            UiState.Success(isLiked)
        }
    }

    suspend fun addChatMessage(message : String, uid: String) = withContext(Dispatchers.IO){
        safeCall {
            val newMessage = Message(message,auth.uid!!)
            realtime.getReference(auth.uid!!).child(uid).child("message" + System.currentTimeMillis()).setValue(newMessage)
            realtime.getReference(uid).child(auth.uid!!).child("message" + System.currentTimeMillis()).setValue(newMessage)
            chatMessages.add(newMessage)
            UiState.Success(chatMessages)
        }
    }

    suspend fun getChatMessages(uid: String)  = withContext(Dispatchers.IO) {
        safeCall {
            val result = realtime.getReference("${auth.uid}/$uid").get().await()
            for(message in result.children) {
                val mes = message.getValue(Message::class.java)
                chatMessages.add(mes!!)
            }
            UiState.Success(chatMessages)
        }

    }

    suspend fun createComment(commentText: String,postId : String) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val commentId = UUID.randomUUID().toString()
            val user = getUser(uid).data!!
            val comment = Comment(
                commentId,
                postId,
                uid,
                user.username,
                user.profilePictureUrl,
                commentText
            )
            commentsRef.document(commentId).set(comment).await()
            UiState.Success(comment)
        }

    }

    suspend fun getComments(postId : String) = withContext(Dispatchers.IO) {
        safeCall {
            val commentsForPosts = commentsRef
                .whereEqualTo("postId",postId)
                .get()
                .await()
                .toObjects(Comment::class.java)
                .onEach { comment ->
                    val user = getUser(comment.uid).data!!
                    comment.username = user.username
                    comment.profilePictureUrl = user.profilePictureUrl
                }
            UiState.Success(commentsForPosts)
        }
    }

    suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO){
        safeCall {
            commentsRef.document(comment.commentId).delete().await()
            UiState.Success(comment)
        }
    }

    private suspend fun updateProfilePicture(uid : String, imageUri : Uri) = withContext(Dispatchers.IO) {
        val user = getUser(uid).data!!
        if(user.profilePictureUrl != DEFAULT_PROFILE_IMAGE){
            storage.getReferenceFromUrl(user.profilePictureUrl).delete().await()
        }

        storage.getReference(uid).putFile(imageUri).await().metadata?.reference?.downloadUrl?.await()
    }

    suspend fun updateProfile(profileUpdate: ProfileUpdate) = withContext(Dispatchers.IO) {
        safeCall {
            val imageUrl = profileUpdate.profilePictureUrl?.let { uri ->
                updateProfilePicture(profileUpdate.uidToProfile,uri).toString()
            }
            val map = mutableMapOf(
                "username" to profileUpdate.username,
                "descriptor" to profileUpdate.description
            )
            imageUrl?.let { url ->
                map["profilePictureUrl"] = url
            }
            usersRef.document(profileUpdate.uidToProfile).update(map.toMap()).await()
            UiState.Success(Any())
        }
    }






}












