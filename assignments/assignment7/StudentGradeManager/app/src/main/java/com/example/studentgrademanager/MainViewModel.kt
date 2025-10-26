package com.example.studentgrademanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
1.
The MainViewModel manages various pieces of data by creating all of these
observable state variables. They're all wrapped in mutable state of and automatically notifies
the UI to update when a value changes. Ultimately this is really useful for testing and debugging specific
functions and pieces of data since you don't potentially ruin something else by changing things here.
 */

class MainViewModel : ViewModel() {

    var students by mutableStateOf<List<Student>>(emptyList())
        private set //'private set' means the 'students' list can be read externally but only modified within this ViewModel.

    var isLoading by mutableStateOf(false)
        private set

    var newStudentName by mutableStateOf("")
        private set

    var newStudentGrade by mutableStateOf("")
        private set

    fun addStudent(name: String, grade: String) {
        //Implements the addStudent function
        //Convert grade to Float, create a Student object, add it to the students list
       val gradeValue = grade.toFloatOrNull() ?: return
        val newStudent = Student(name, gradeValue)
        students = students + newStudent
    }

    fun removeStudent(student: Any) {
        //Implements the removeStudent function
        //It should take a 'Student' object as a parameter and remove it from the students list.
        students = students.filter { it != student }
    }

    fun calculateGPA(): Float {
        //Implements the calculateGPA functions
        //It should return a Float, calculate the average grade of all students, and return 0f if the list is empty.
        if (students.isEmpty()) return 0f
        val total = students.sumOf { it.grade.toDouble() }
        return (total / students.size).toFloat()
    }

    fun loadSampleData() {
        //Implements the loadSampleData function using coroutines (viewModelScope.launch)
        viewModelScope.launch {
            isLoading = true
            delay(1500)
            students = listOf(
                Student("Alice Johnson", 95f),
                        Student("Bob Smith", 87f),
                        Student("Carol Davis", 92f)
            )
            isLoading = false

        }
    }

    fun updateNewStudentName(name: String) {
        //Implements the updateNewStudentName function
        //It should take a 'name' (String) as a parameter and update newStudentName.
        newStudentName = name
    }

    fun updateNewStudentGrade(grade: String) {
        //Implements the updateNewStudentGrade function
        //It should take a 'grade' (String) as a parameter and update newStudentGrade.
        newStudentGrade = grade
    }
}
/*
data class Student(
    val name: String,    Got an error saying I can only have 1 data class so I removed this one
    val grade: Float
)
*/


/*
3.
So the lifecycle of our two files works something like this. Once the MainActivity calls the
 viewModel() it will create the MainViewModel, after that it becomes capable of persisting
 after recompilation like the screen changing while the MainActivity is actually being recreated.
 This is super useful because it allows us to preserve all state changes without needing to manually
 save them beforehand.
 */

