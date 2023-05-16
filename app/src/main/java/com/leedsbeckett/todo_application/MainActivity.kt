package com.leedsbeckett.todo_application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var adapter: TodoAdapter
    private lateinit var rvTodos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTask = findViewById(R.id.etTodo)
        buttonAddTask = findViewById(R.id.btnAddTodo)
        taskListView = findViewById(R.id.lvTodos)
        rvTodos = findViewById(R.id.rvTodos)

        val taskDatabase = TaskDatabase.getDatabase(this)
        taskDao = taskDatabase.taskDao()

        // Restore the task list from the database
        launch {
            taskList = ArrayList(taskDao.getAllTasks())
            withContext(Dispatchers.Main) {
                updateTaskList()
                setupRecyclerView()
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
                        adapter.notifyItemInserted(taskList.size - 1)
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

    private fun setupRecyclerView() {
        adapter = TodoAdapter(taskList)
        rvTodos.adapter = adapter
        rvTodos.layoutManager = LinearLayoutManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
