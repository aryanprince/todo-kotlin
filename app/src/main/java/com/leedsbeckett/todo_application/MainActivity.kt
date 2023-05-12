package com.leedsbeckett.todo_application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var taskListView: ListView

    private var taskList = ArrayList<String>()

    companion object {
        private const val TASK_LIST_KEY = "task_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAddTask = findViewById(R.id.buttonAddTask)
        taskListView = findViewById(R.id.listViewTasks)

        // Check if bundle has any saved tasks, if yes add them to the array
        taskList = savedInstanceState?.getStringArrayList(TASK_LIST_KEY) ?: ArrayList()
        updateTaskList()

        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString()
            if (task.isNotEmpty()) {
                taskList.add(task)
                editTextTask.text.clear()
                updateTaskList()
            }
        }
    }

    private fun updateTaskList() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putStringArrayList(TASK_LIST_KEY, taskList)
    }
}
