package com.example.interactivebuttonassignment

import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                InteractiveButtonGrid();  //<-- Added this to call main composable
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveButtonGrid() {

    var selectedButtons by remember { mutableStateOf(setOf<Int>()) }

    /* ^^^ 1. We Created a state variable above with mutable state of and used the Set of Int type
     We add the Mutable state of because it creates an observable state that will trigger an action such as our buttons
     we need to add remember in order for the machine to preserve that information once it re-compiles to not reset it
     using a normal variable wouldn't work because the data would constantly be resetting
    */



    // List of button data (color, number) - already provided
    val buttonData = listOf(
        ButtonData(Color(0xFFE57373), "1"), // Red
        ButtonData(Color(0xFF81C784), "2"), // Green
        ButtonData(Color(0xFF64B5F6), "3"), // Blue
        ButtonData(Color(0xFFFFB74D), "4"), // Orange
        ButtonData(Color(0xFFBA68C8), "5"), // Purple
        ButtonData(Color(0xFF4DB6AC), "6"), // Teal
        ButtonData(Color(0xFFFF8A65), "7"), // Deep Orange
        ButtonData(Color(0xFF90A4AE), "8"), // Blue Grey
        ButtonData(Color(0xFFF06292), "9"), // Pink
        ButtonData(Color(0xFF7986CB), "10"), // Indigo
        ButtonData(Color(0xFF4DD0E1), "11"), // Cyan
        ButtonData(Color(0xFFFFD54F), "12"), // Yellow
        ButtonData(Color(0xFF8D6E63), "13"), // Brown
        ButtonData(Color(0xFF9575CD), "14"), // Deep Purple
        ButtonData(Color(0xFF4FC3F7), "15"), // Light Blue
        ButtonData(Color(0xFF66BB6A), "16"), // Light Green
        ButtonData(Color(0xFFFFCC02), "17"), // Amber
        ButtonData(Color(0xFFEC407A), "18"), // Pink
        ButtonData(Color(0xFF42A5F5), "19"), // Blue
        ButtonData(Color(0xFF26A69A), "20"), // Teal
        ButtonData(Color(0xFFFF7043), "21"), // Deep Orange
        ButtonData(Color(0xFF9CCC65), "22"), // Light Green
        ButtonData(Color(0xFF26C6DA), "23"), // Cyan
        ButtonData(Color(0xFFD4E157), "24")  // Lime
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)     //<-- Added these columns
            .padding(top = 20.dp)


        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Interactive Button Grid",
            style = MaterialTheme.typography.headlineMedium ,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        //^^^ Created this Title text, with padding and style

        Text(
            text = "Selected: ${selectedButtons.size} of ${buttonData.size}",
            style = MaterialTheme.typography.bodyLarge ,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        //^^^ Created this selection count text to track what we picked

        FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                verticalArrangement = Arrangement.spacedBy(8.dp) ,
                modifier = Modifier.fillMaxWidth() ,
            ) {
            buttonData.forEachIndexed { index, button ->
                InteractiveButton(
                    buttonData = button,
                    isSelected = selectedButtons.contains(index) ,
                    onClick = { /* handle selection logic */}
                )
            }
        }
        /*^^^ 2. Created a FlowRow above along with an parameters to create interactive buttons
        There are a lot of advantages to using FlowRow over row or column such as
        Automatic wrapping so you don't have to scroll around the screen for buttons
        Adapts to different screen sizes without the use of overflow and allows for better spacing
        The problem with just using Row  would be on smaller screens the buttons could be cutoff
        */

        Button(
            onClick = { selectedButtons = setOf() } ,
            enabled = selectedButtons.isNotEmpty() , //
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Clear Selection"
            )
        }
        //^^^ Created this clear selection button in order to clear our currently selected choices
        // Basically a reset button


        Spacer(modifier = Modifier.height(24.dp))


    }
}

@Composable
fun InteractiveButton(
    buttonData: ButtonData,
    isSelected: Boolean,
    onClick: () -> Unit

    /* ^^^ 3. So we make interactive button it's own composable for multiple reasons but the
        main one being reusability and readability. It's overall just easier to maintain button logic
        when it's isolated and you can now use this button code anywhere else in the app
        similarly to making a new class in Java
    */

) {
    Box(
        modifier = Modifier

            .size(80.dp)

            /* vvv 4A. Background will fill the entire shape of the...background with a solid color
            Below We need to make an if else statement in order to background colors Depending on our selections
            will use our buttonData color if nothings chosen
            Shape will set our background size
            .primaryContainer will effect backgrounds for our chips or containers
            */

            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    buttonData.color
                } ,
                shape = MaterialTheme.shapes.medium
            )

            /*vvv 4B. Border is specifically just for the outline or edges of a shape we use it on
             Setting our border thickness and choosing color based depending on our selection
            Shape will gives us our smooth edges
            For color the 0.3f will make our selection transparent at 30% opacity
            .primary will effect primary buttons
            */
            .border(
                width = if (isSelected) 3.dp else 1.dp,

                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Black.copy(alpha = 0.3f)
                } ,
                shape = MaterialTheme.shapes.medium
            )

            .clickable { onClick() }
        ,

        // 4C. Together we use border and background to create buttons with a colored background with borders
        // That will change based on our selections


        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonData.number,

            // 5. It's better to group related information because it's good code design to keep data
            // together so it can be easier to maintain and access along with making testing easier

            //vvv Same concept as all the other if else statements before
            //onPrimaryContainer will effect our text icons and other foreground elements
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                Color.White
            },


            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Data class to hold button information - already provided
data class ButtonData(
    val color: Color,
    val number: String
)

/**
 * Preview for Android Studio's design view.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InteractiveButtonGridPreview() {
    InteractiveButtonGrid()
}