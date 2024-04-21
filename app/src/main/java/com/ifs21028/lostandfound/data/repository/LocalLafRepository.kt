package com.ifs21028.lostandfound.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.ifs21028.lostandfound.data.local.entity.LafEntity
import com.ifs21028.lostandfound.data.local.room.ILafDao
import com.ifs21028.lostandfound.data.local.room.LafDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LocalLafRepository(context: Context) {
    private val mLafDao: ILafDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = LafDatabase.getInstance(context)
        mLafDao = db.lafDao()
    }
    fun getAllTodos(): LiveData<List<LafEntity>?> = mLafDao.getAllTodos()
    fun get(todoId: Int): LiveData<LafEntity?> = mLafDao.get(todoId)
    fun insert(todo: LafEntity) {
        executorService.execute { mLafDao.insert(todo) }
    }
    fun delete(todo: LafEntity) {
        executorService.execute { mLafDao.delete(todo) }
    }
    companion object {
        @Volatile
        private var INSTANCE: LocalLafRepository? = null
        fun getInstance(
            context: Context
        ): LocalLafRepository {
            synchronized(LocalLafRepository::class.java) {
                INSTANCE = LocalLafRepository(
                    context
                )
            }
            return INSTANCE as LocalLafRepository
        }
    }
}
