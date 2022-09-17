package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long):Post
    fun delLikeById(id: Long): Post
    fun save(post: Post)
    fun removeById(id: Long)
}