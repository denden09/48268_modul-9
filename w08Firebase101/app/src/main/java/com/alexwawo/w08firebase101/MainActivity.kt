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
    var editingDocId by remember { mutableStateOf<String?>(null) }
    Column(...) {
    ...
    if (phoneList.isNotEmpty()) {
        Text("Phone Numbers:", style =
            MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top =
            12.dp))
        phoneList.forEachIndexed { index, phone ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                TextField(
                    value = phone,
                    onValueChange = {
                        phoneList = phoneList.toMutableList().also {
                                list -> list[index] = it }
                    },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        phoneList = phoneList.toMutableList().also {
                            it.removeAt(index) }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Delete")
                }
            }
        }
    }
    Button(
        onClick = {
            if (editingDocId != null) {
                viewModel.updateStudent(Student(studentId, name,
                    program, phoneList, editingDocId!!))
                editingDocId = null
            } else {
                viewModel.addStudent(Student(studentId, name,
                    program, phoneList))
            }
            studentId = ""
            name = ""
            program = ""
            phoneList = listOf()
        },
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Text(if (editingDocId != null) "Update" else "Submit")
    }
    Divider(modifier = Modifier.padding(vertical = 16.dp))
    Text("Student List", style =
        MaterialTheme.typography.titleMedium)
    LazyColumn {
        items(viewModel.students) { student ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text("ID: ${student.id}")
                Text("Name: ${student.name}")
                Text("Program: ${student.program}")
                if (student.phones.isNotEmpty()) {
                    Text("Phones:")
                    student.phones.forEach {
                        Text("- $it", style =
                            MaterialTheme.typography.bodySmall)
                    }
                }
                Row {
                    Button(onClick = {
                        studentId = student.id
                        name = student.name
                        program = student.program
                        phoneList = student.phones
                        editingDocId = student.docId
                    }) {
                        Text("Edit")
                    }
                    Button(
                        onClick = { viewModel.deleteStudent(student)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Delete")
                    }
                }
                Divider()
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