package com.example.studytimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StudyTimerApp()
            }
        }
    }
}

@Composable
fun StudyTimerApp() {

    var isRunning by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(2700) } // 45 minutes in seconds
    var sessionLength by remember { mutableStateOf(45) } //goes up to 45 minutes
    var completedSessions by remember { mutableStateOf(0) } //end of timer

    /* 1. Here we use mutable states so the UI updates when values change.
    *  The benefits of this would be automating processes and that the state persists through recomposition.
    *  These apply to our "isRunning" by changing Start to Reset and for out timeRemaining it will update the timer.
    * */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text( //App Title
            text = "Worlds Best Study Timer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        TimerDisplay( //Timer
            timeRemaining = timeRemaining,
            sessionLength = sessionLength
        )



        Spacer(modifier = Modifier.height(32.dp))

        TimerControls( //Calling Time control composable
            isRunning = isRunning,
            onToggleTimer = {
                if (isRunning) { //Resets timer
                    isRunning = false
                    timeRemaining = sessionLength * 60
                } else { //Start timer here
                    isRunning = true
                }
            }
        )


        Spacer(modifier = Modifier.height(32.dp))

        SessionSettings( //Call our Settings composable
            sessionLength = sessionLength,
            onSessionLengthChange = {
                //Below is our lambda which takes our newLength and updates sessionLength
                //but only conditionally will update our time remaining
                newLength -> sessionLength = newLength
                if (!isRunning) {
                    timeRemaining = newLength * 60
                    //If we remove the if statement then the timer resets when swapping to another timer
                }
            }
        )


        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Completed Sessions: $completedSessions",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }

    LaunchedEffect(isRunning) {

        /* 3. So we implement our launch effect by adding the parameter isRunning which
        will the effect change once the start is initiated. The main consideration to take is
        what effects you want to follow once the program stops running, for us we are adding to
        completed sessions for example here once we hit the end of the timer.

        5. The role of launched effects is to manage side effects when isRunning is triggered.
        it does this byt triggering a recomposition every second based on timeremaining-- This will specifically
        help managing certain operations in the background and apparently helps with memory leaks even.
         */

        while (isRunning && timeRemaining >0) {
            delay(1000)
            timeRemaining--

            if (timeRemaining == 0) {
                isRunning = false
                completedSessions++
                timeRemaining = sessionLength * 60
            }
        }
    }
}

@Composable
fun TimerDisplay(
    timeRemaining: Int,
    sessionLength: Int

    /* 2. The key differences between stateless and stateful composables
    is that stateless are easier to test and recieve data through cetain perameters while stateful
    manages it's own logic internally like with mutable states. It's good for this project to just use stateless
    because we just need it to display data here and not manage it.
       */
) {
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val totalSeconds = sessionLength * 60
    val progress = if (totalSeconds > 0) {
        ((totalSeconds - timeRemaining).toFloat() / totalSeconds * 100).toInt()
    } else {
        0
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = String.format("%02d:%02d", minutes, seconds, progress), //Maybe remove progress
            //Above %02d displays ints with at least 2 numbers and uses padding if there are 0'
            //The : adds the colon between the min and sec giving us EX. "05:03"
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${progress.toInt()}% Complete",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}

@Composable
fun TimerControls(
    isRunning: Boolean,
    onToggleTimer: () -> Unit
) {

    Button(
        onClick = onToggleTimer,
        modifier = Modifier
            .width(120.dp) //Change this to normal width(dp)
            .height(56.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = if (isRunning) "Reset" else "Start",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

    }

}

/* 4. So we like to keep our TimerControls and SessionSettings seperate for a lot of reasons
but the main ones just being testability and readability. (Close tab as example) It does also improve
recomposition in some ways.
 */


@Composable
fun SessionSettings(
    sessionLength: Int,
    onSessionLengthChange: (Int) -> Unit
) {

    val sessionOption = listOf(5,15,25,45)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Session Length: $sessionLength minutes", //Delete session length as example
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

    Spacer(modifier = Modifier.height(16.dp))

    Row (
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        //Check this
        sessionOption.forEach { option ->
            Button(
                onClick = { onSessionLengthChange(option) },
                modifier = Modifier.width(70.dp),
                colors = if (sessionLength == option) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary

                    )
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text(
                    text = "${option}", //Add letter here to show changes
                    fontSize = 14.sp
                )
             }
          }
        }
     }
}

@Preview(showBackground = true)
@Composable
fun StudyTimerPreview() {
    StudyTimerApp()
}