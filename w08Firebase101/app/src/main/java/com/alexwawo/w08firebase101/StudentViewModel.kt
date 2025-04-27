package com.alexwawo.w08firebase101

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Student(
    val docId: String = "",
    val id: String = "",
    val name: String = "",
    val program: String = "",
    val phones: List<String> = listOf()
)

class StudentViewModel : ViewModel() {
    private val db = Firebase.firestore
    var students by mutableStateOf(listOf<Student>())
        private set

    init {
        fetchStudents()
    }

    fun addStudent(student: Student) {
        val studentMap = hashMapOf(
            "id" to student.id,
            "name" to student.name,
            "program" to student.program,
            "phones" to student.phones
        )

        db.collection("students")
            .add(studentMap)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot added with ID: ${it.id}")
                fetchStudents() // Refresh list
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    fun updateStudent(student: Student) {
        val updatedData = hashMapOf(
            "id" to student.id,
            "name" to student.name,
            "program" to student.program,
            "phones" to student.phones
        )
        db.collection("students")
            .document(student.docId)
            .set(updatedData)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot updated with ID: ${student.docId}")
                fetchStudents()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating document", e)
            }
    }

    fun deleteStudent(student: Student) {
        db.collection("students").document(student.docId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Student deleted")
                fetchStudents()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error deleting student", it)

            }
    }
    fun updateStudent(student: Student) {
        val studentMap = mapOf(
            "id" to student.id,
            "name" to student.name,
            "program" to student.program
        )
        val studentDocRef = db.collection("students").document(student.docId)
        studentDocRef.set(studentMap)
            .addOnSuccessListener {
                val phonesRef = studentDocRef.collection("phones")
                // Step 1: Delete old phones
                phonesRef.get().addOnSuccessListener { snapshot ->
                    val deleteTasks = snapshot.documents.map {
                        it.reference.delete() }
                    // Step 2: When all phones deleted, add new phones

                    com.google.android.gms.tasks.Tasks.whenAllComplete(deleteTasks)
                        .addOnSuccessListener {
                            val addPhoneTasks = student.phones.map { phone ->
                                val phoneMap = mapOf("number" to phone)
                                phonesRef.add(phoneMap)
                            }
                            // Step 3: After all new phones added, fetch
                            updated list

                                    com.google.android.gms.tasks.Tasks.whenAllComplete(addPhoneTasks)
                                        .addOnSuccessListener {
                                            fetchStudents()
                                        }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating student", e)
            }
    }

    private fun fetchStudents() {
        db.collection("students")
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Student>()
                for (document in result) {
                    val docId = document.id
                    val id = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val program = document.getString("program") ?: ""
                    val phones = document.get("phones") as? List<String> ?: listOf()
                    list.add(Student(docId, id, name, program, phones))
                }
                students = list
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
            }
    }
}

@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel = viewModel()) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    var phoneList by remember { mutableStateOf(listOf<String>()) }
    var selectedStudentDocId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Form fields untuk ID, Name, Program, Phone List (tidak diketik lengkap di contoh ini)

        if (phoneList.isNotEmpty()) {
            phoneList.forEachIndexed { index, phone ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("- $phone", modifier = Modifier.weight(1f))
                    Button(onClick = {
                        phoneList = phoneList.toMutableList().also {
                            it.removeAt(index)
                        }
                    }) {
                        Text("Remove")
                    }
                }
            }
        }

        Button(
            onClick = {
                if (selectedStudentDocId != null) {
                    viewModel.updateStudent(Student(selectedStudentDocId!!, studentId, name, program, phoneList))
                    selectedStudentDocId = null
                } else {
                    viewModel.addStudent(Student("", studentId, name, program, phoneList))
                }
                // Reset form
                studentId = ""
                name = ""
                program = ""
                phoneList = listOf()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (selectedStudentDocId != null) "Update" else "Submit")
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(viewModel.students) { student ->
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)) {
                    Text("ID: ${student.id}")
                    Text("Name: ${student.name}")
                    Text("Program: ${student.program}")
                    student.phones.forEach { phone ->
                        Text("- $phone")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            studentId = student.id
                            name = student.name
                            program = student.program
                            phoneList = student.phones
                            selectedStudentDocId = student.docId
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
}
