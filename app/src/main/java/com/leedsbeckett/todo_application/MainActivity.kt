package com.leedsbeckett.todo_application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var editTextTask: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var taskListView: ListView
    private lateinit var taskList: ArrayList<Task>
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAddTask = findViewById(R.id.buttonAddTask)
        taskListView = findViewById(R.id.listViewTasks)

        val taskDatabase = TaskDatabase.getDatabase(this)
        taskDao = taskDatabase.taskDao()

        // Restore the task list from the database
        launch {
            taskList = ArrayList(taskDao.getAllTasks())
            withContext(Dispatchers.Main) {
                updateTaskList()
            }
        }

        buttonAddTask.setOnClickListener {
            val taskName = editTextTask.text.toString()
            if (taskName.isNotEmpty()) {
                val newTask = Task(name = taskName)

                // Actually adding the new task to database
                launch {
                    taskDao.insertTask(newTask)
                    taskList.add(newTask)
                    withContext(Dispatchers.Main) {
                        updateTaskList()
                    }
                }
                editTextTask.text.clear()
            }
        }
    }

    private fun updateTaskList() {
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList.map { it.name })
        taskListView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
