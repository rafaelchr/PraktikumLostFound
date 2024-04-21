package com.ifs21028.lostandfound.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "lost_found")
data class LafEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @ColumnInfo(name = "user_id")
    var userId: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "status")
    var status: String,

    @ColumnInfo(name = "is_completed")
    var isCompleted: Int,

    @ColumnInfo(name = "cover")
    var cover: String?,

    @ColumnInfo(name = "created_at")
    var createdAt: String,

    @ColumnInfo(name = "updated_at")
    var updatedAt: String,

) : Parcelable