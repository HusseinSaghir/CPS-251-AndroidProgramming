package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                WeatherDashboard()
            }
        }
    }
}


@Composable
fun WeatherDashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrentWeatherSection()
    }
}


@Composable
fun CurrentWeatherSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp)

    ) {  Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Actually call the composables here
        WeatherInformation()
        TextRows()
    }

        // Place the alert overlay on top
        WeatherAlertOverlay()
    }
}


        @Composable
        fun WeatherAlertOverlay() {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier.padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Severe Weather",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }



        @Composable
        fun WeatherInformation() {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("72 F", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Partly Cloudy", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Ann Arbor, MI", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        @Composable
        fun TextRows() {

            Row(
                modifier = Modifier.padding(top =16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Text("H: 78 F", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("L: 65 F", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }






/**
 * Preview for Android Studio's design view.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherDashboardPreview(){
    WeatherDashboard()
}


/**
 * This project covers concepts from Chapter 4 lessons:
 * - "Box Layout" - for layered content and overlays
 * - "Column Layout" - for vertical content arrangement
 * - "Row Layout" - for horizontal content arrangement
 * - "Layout Basics" - for fundamental layout concepts and styling
 *
 * Students should review these lessons before starting:
 * - Box Layout lesson for overlay and layered content
 * - Column Layout lesson for vertical arrangement
 * - Row Layout lesson for horizontal arrangement
 * - Layout Basics lesson for fundamental concepts
 */