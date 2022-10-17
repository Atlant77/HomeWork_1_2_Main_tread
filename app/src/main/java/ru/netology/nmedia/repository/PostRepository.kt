package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: Callback<List<Post>>)
    fun likeByIdAsync(id: Long, callback: Callback<Post>)
    fun delLikeByIdAsync(id: Long, callback: Callback<Post>)
    fun saveAsync(post: Post, callback: Callback<Post>)
    fun removeByIdAsync(id: Long, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(posts: T){}
        fun onError(e: Exception){}
    }
}
