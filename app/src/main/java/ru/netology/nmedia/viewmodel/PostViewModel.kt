package ru.netology.nmedia.viewmodel

import  android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.Exception

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(post = it, object : PostRepository.Callback<Post> {
                override fun onSuccess(posts: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    onError(e)
                }
            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        val postToLike = _data.value?.posts.orEmpty().filter { it.id == id }
        if (!postToLike[0].likedByMe) {
            repository.likeByIdAsync(id, object : PostRepository.Callback<Post> {
                override fun onSuccess(postFromServer: Post) {
                    val postsNewLiked = _data.value?.posts.orEmpty().map {
                        if (it.id === postFromServer.id) {
                            Post(
                                id = postFromServer.id,
                                author = postFromServer.author,
                                content = postFromServer.content,
                                published = postFromServer.published,
                                likedByMe = postFromServer.likedByMe,
                                likes = postFromServer.likes
                            )
                        } else it
                    }
                    println(postFromServer)
                    _data.postValue(FeedModel(posts = postsNewLiked))
                }

                override fun onError(e: Exception) {
                    onError(e)
                }
            })
        } else {
            repository.delLikeByIdAsync(id, object : PostRepository.Callback<Post> {
                override fun onSuccess(postFromServer: Post) {
                    val postsNewLiked = _data.value?.posts.orEmpty().map {
                        if (it.id === postFromServer.id) {
                            Post(
                                id = postFromServer.id,
                                author = postFromServer.author,
                                content = postFromServer.content,
                                published = postFromServer.published,
                                likedByMe = postFromServer.likedByMe,
                                likes = postFromServer.likes
                            )
                        } else it
                    }
                    _data.postValue(FeedModel(posts = postsNewLiked))
                }

                override fun onError(e: Exception) {
                    onError(e)
                }
            })
        }
    }

    fun removeById(id: Long) {
        // Оптимистичная модель
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            )
        )
        repository.removeByIdAsync(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(posts: Post) {
                _postCreated.postValue(Unit)
            }

            override fun onError(e: Exception) {
                onError(e)
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }
}
