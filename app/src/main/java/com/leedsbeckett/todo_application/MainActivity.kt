package com.leedsbeckett.todo_application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.leedsbeckett.todo_application.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDao: TaskDao
    private lateinit var tasks: ArrayList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskDao = TaskDatabase.getDatabase(this).taskDao()

        // Restore the task list from the database
        launch { fetchTasksFromDatabase() }

        binding.btnAddTodo.setOnClickListener { addNewTask() }
    }

    private suspend fun fetchTasksFromDatabase() {
        tasks = ArrayList(taskDao.getAllTasks())
        withContext(Dispatchers.Main) {
            taskAdapter = TaskAdapter(tasks)
            binding.rvTodos.adapter = taskAdapter
            binding.rvTodos.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun addNewTask() {
        val taskName = binding.etTodo.text.toString()
        if (taskName.isNotEmpty()) {
            val newTask = Task(name = taskName)

            // Actually adding the new task to database
            launch {
                taskDao.insertTask(newTask)
                tasks.add(newTask)
                withContext(Dispatchers.Main) {
                    taskAdapter.notifyItemInserted(tasks.size - 1)
                }
            }
            binding.etTodo.text.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
