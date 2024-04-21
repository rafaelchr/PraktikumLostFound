package com.ifs21028.lostandfound.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ifs21028.lostandfound.data.local.entity.LafEntity
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.AuthorGetAllLafResponse
import com.ifs21028.lostandfound.data.remote.response.LostFoundsItemResponse

class Utils {
    companion object {
        fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
            val observerWrapper = object : Observer<T> {
                override fun onChanged(value: T) {
                    observer(value)
                    if (value is MyResult.Success<*> ||
                        value is MyResult.Error
                    ) {
                        removeObserver(this)
                    }
                }
            }
            observeForever(observerWrapper)
        }
        fun entitiesToResponses(entities: List<LafEntity>):
                List<LostFoundsItemResponse> {
            val responses = ArrayList<LostFoundsItemResponse>()
            entities.map {
                val author = AuthorGetAllLafResponse(
                    name = "Nama Penulis",
                    photo = "URL_Foto_Penulis"
                )

                val response = LostFoundsItemResponse(
                    cover = it.cover,
                    updatedAt = it.updatedAt,
                    userId = it.userId,
                    author = author,
                    description = it.description,
                    createdAt = it.createdAt,
                    id = it.id,
                    title = it.title,
                    isCompleted = it.isCompleted,
                    status = it.status,
                )
                responses.add(response)
            }
            return responses
        }
    }
}



