package com.alexwawo.w08firebase101

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexwawo.w08firebase101.ui.theme.W08Firebase101Theme
import androidx.compose.foundation.lazy.items
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            MaterialTheme {
                StudentRegistrationScreen()
            }
        }
    }
}

@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel = viewModel())
{
    ...
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        ...
        Button(
            onClick = {
                if (editingStudent == null) {
                    viewModel.addStudent(Student(studentId, name,
                        program))
                } else {
                    viewModel.updateStudent(Student(studentId, name,
                        program, editingStudent!!.docId))
                    editingStudent = null
                }
                studentId = ""
                name = ""
                program = ""
            }
        ) {
            Text(if (editingStudent == null) "Submit" else "Update")
        }
        ...
        LazyColumn {
            items(viewModel.students) { student ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("${student.id} - ${student.name} -
                        ${student.program}")
                    Button(onClick = {
                        studentId = student.id
                        name = student.name
                        program = student.program
                        editingStudent = student
                    }) {
                        Text("Edit")
                    }
                    Button(onClick = {
                        viewModel.deleteStudent(student)
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}




@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    W08Firebase101Theme {
        Greeting("Android")
    }
}