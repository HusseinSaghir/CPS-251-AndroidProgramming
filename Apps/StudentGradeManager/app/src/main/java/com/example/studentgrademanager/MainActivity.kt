package com.example.studentgrademanager

// Core Android imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Compose UI imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ViewModel imports
import androidx.lifecycle.viewmodel.compose.viewModel


/*
2.
The interaction between our StudentGradeManager Composable and the MainViewModel
that demonstrates a unidirectional data flow would be how the Composable reads states from the ViewModel
such as the (viewModel.students and viewModel.newStudentName) and sends events through the callBack functions
 we set up, which uses that information to display them.
The viewModel also updates its state which is useful for us since this will trigger the UI
to adjust and recompile with state changes such as flipping the screen horizontally.
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MaterialTheme {
                Surface {
                    StudentGradeManager()
                }
            }
        }
    }
}

//Student Data Class
data class Student (
    val name: String,
    val grade: Float
)


@Composable
fun StudentGradeManager(
    viewModel: MainViewModel = viewModel()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 50.dp)
    ) {
        // Header
        item {
            Text(
                text = "Student Grade Manager",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }


        // GPADisplay
        item {
            GPADisplay(gpa = viewModel.calculateGPA())
        }


        // Student Form
        item {
            AddStudentForm(
                name = viewModel.newStudentName,
                grade = viewModel.newStudentGrade,
                onNameChange = { viewModel.updateNewStudentName(name = it) },
                onGradeChange = { viewModel.updateNewStudentGrade(grade = it)},
                onAddStudent =  { viewModel.addStudent(viewModel.newStudentName, viewModel.newStudentGrade)
                }
            )
        }

        //Load Sample Data Button
        item {
            Button(
                onClick = { viewModel.loadSampleData()},
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Load Sample Data")
            }
        }

        item {
            StudentsList(
                students = viewModel.students,
                onRemoveStudent = { student -> viewModel.removeStudent(student) }
            )
        }

        //Loading Icon
        if(viewModel.isLoading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun GPADisplay(gpa: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Class GPA",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = String.format("%.2f", gpa),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AddStudentForm(
    name: String,
    grade: String,
    onNameChange: (String) -> Unit,
    onGradeChange: (String) -> Unit,
    onAddStudent: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
           Text(
               text = "Add New Student",
               style = MaterialTheme.typography.titleMedium,
               modifier = Modifier.padding(bottom = 8.dp)
           )

            //For Name input
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Student Name")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            //For Grade input
            OutlinedTextField(
                value = grade,
                onValueChange = onGradeChange,
                label = { Text("Grade (0-100)")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Button To Add Student
            //We also set parameters to make sure name and grade is not blank
            Button(
                onClick = onAddStudent,
                enabled = name.isNotBlank() && grade.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text ("Add Student")
            }
        }
    }
}

/*
4.
The key advantages to using a LazyColumn for displaying the list of students over using a regular one
would be that it helps with performance saving memory by only rendering currently visible items and
helps with scalability since it should be capable of handling thousands of items now. Also apparently
it has built in scrolling behavior without needing modifiers!
 */

@Composable
fun StudentsList(
    students: List<Student>, //Changed Any to Student after defining Student data class
    onRemoveStudent: (Student) -> Unit //Changed Any to Student after defining Student data class
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .heightIn(max = 300.dp) //Limit height to prevent overflow
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //Text composable for "Students
            Text(
                text = "Students (${students.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            //Text composable for "No students added yet" if the list is empty
            if (students.isEmpty()) {
              Text(
                  text = "No students added yet",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
              )


            } else {
             LazyColumn (
                 modifier = Modifier.heightIn(max = 200.dp),
                 verticalArrangement = Arrangement.spacedBy(4.dp)
             ){
                 itemsIndexed(students) {
                     index, student -> StudentRow(
                         student = student,
                         onRemove = { onRemoveStudent(student) }
                     )
                 if(index < students.size - 1){
                     HorizontalDivider()
                 }
                 }
             }

            }
        }
    }
}

@Composable
fun StudentRow(
    student: Student, //Changed Any to Student after defining Student data class
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            //Added a Text composable to display student.name
            Text(
                text = student.name,
                style = MaterialTheme.typography.bodyLarge
            )

            //Add a Text composable to display "Grade: ${student.grade}"
           Text(
               text = "Grade: ${student.grade}",
               style = MaterialTheme.typography.bodyMedium,
               color = MaterialTheme.colorScheme.onSurfaceVariant
           )
        }

        //Created an IconButton with Icons.Default.Delete for removing a student
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove Student"
            )
        }

    }
}

/**
 * Preview function for the StudentGradeManager screen
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentGradeManagerPreview() {
    MaterialTheme {
    }
}

/*
5.
The design of our application offers a lot of benefits ranging from scalability,
readability, easier testing and makes it a lot more easier for new programmers to be able to find specific
details they may need to fix or update the code in order to improve it. Our MainActivity acts
as an entrance point and sets up UI environments while MainViewModel manages logic, states and
other data operations.
 */