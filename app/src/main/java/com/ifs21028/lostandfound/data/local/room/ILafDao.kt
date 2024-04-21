package com.ifs21028.lostandfound.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifs21028.lostandfound.data.local.entity.LafEntity

@Dao
interface ILafDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(delcomTodo: LafEntity)

    @Delete
    fun delete(delcomTodo: LafEntity)

    @Query("SELECT * FROM lost_found WHERE id = :id LIMIT 1")
    fun get(id: Int): LiveData<LafEntity?>

    @Query("SELECT * FROM lost_found ORDER BY created_at DESC")
    fun getAllTodos(): LiveData<List<LafEntity>?>
}
