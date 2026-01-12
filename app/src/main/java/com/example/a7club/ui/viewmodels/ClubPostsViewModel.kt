package com.example.a7club.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.ui.screens.Announcement
import com.example.a7club.ui.screens.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClubPostsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // EKSİK OLAN DEĞİŞKEN EKLENDİ
    var selectedImageUri by mutableStateOf<Uri?>(null)

    val posts = mutableStateListOf<Post>()
    val announcements = mutableStateListOf<Announcement>()

    init {
        fetchPosts()
        fetchAnnouncements()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val fetchedPosts = snapshot.documents.map { doc ->
                    Post(
                        id = doc.id,
                        clubName = doc.getString("clubName") ?: "Bilinmeyen Kulüp",
                        text = doc.getString("text") ?: "",
                        imageUri = doc.getString("imageUri")?.let { Uri.parse(it) },
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                posts.clear()
                posts.addAll(fetchedPosts)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchAnnouncements() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("announcements")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val fetchedAnnouncements = snapshot.documents.map { doc ->
                    Announcement(
                        id = doc.id,
                        clubName = doc.getString("clubName") ?: "Bilinmeyen Kulüp",
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: "",
                        date = doc.getString("date") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                announcements.clear()
                announcements.addAll(fetchedAnnouncements)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}