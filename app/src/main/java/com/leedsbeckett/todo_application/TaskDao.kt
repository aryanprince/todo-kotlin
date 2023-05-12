package com.leedsbeckett.todo_application

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<Task>
}