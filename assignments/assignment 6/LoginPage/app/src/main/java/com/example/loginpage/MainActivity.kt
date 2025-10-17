package com.example.loginpage

// Core Android imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat

// Compose UI imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Navigation imports
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure the window to use light status bar icons
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true


        setContent {
            MaterialTheme {
                Surface {
                    LoginApp()
                }
            }
        }
    }
}


@Composable
fun LoginApp() {

    val navController = rememberNavController()

    /*
    1. The main purpose of using NavController is this will allow us to better navigate the stack and
    handles navigation commands like navigate() or popBackStack(). So for example, below where we have our
    navController under LoginScreen, this is telling the NavController to go to that destination and add it to the back of the stack

    The NavHost container is using startDestination = "login" to display our login screen once we open the app
    and linking our navController
     */

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userName ->
                    navController.navigate("welcome/$userName") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true

                    }
                }
            )
        }


    composable(
        route = "welcome/{userName}",
        arguments = listOf(navArgument("userName") { type = NavType.StringType })
    ) { backStackEntry ->
        val userName = backStackEntry.arguments?.getString("userName") ?: ""
        WelcomeScreen(
            userName = userName,
            onViewProfile = {
                navController.navigate("profile/$userName")
            },
            onLogout = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }

        /*
        2. Above and below we have 2 composables that use a routing mechanism that takes a parameter
        such as welcome/{so and so} and establishes a pattern with them and uses the NavBackStackEntry to
        pass data. What that means is it is creating navigation's to various screens.

        We are using the stack in a way where once we access the profile screen from the home one then it will
        push the home screen back the stack and we implement popBackStack() to be able to pop back over to
        our previous screen again
         */

    composable(
        route = "profile/{userName}",
                arguments = listOf (navArgument("userName") { type = NavType.StringType })
    ) { backStackEntry ->
        val userName = backStackEntry.arguments?.getString("userName") ?: ""
        ProfileScreen(
            userName = userName,
            onBackToWelcome = {
                navController.popBackStack()
            }
        )
    }
  }
}

/*
3. The advantages of organizing UI into distinct composables will ultimately lead to two main reason which
are readability and scalability. It would just be extremely unrealistic to have extremely lengthy code be all in one
file, it can be done sure but it's not very professional. If this code was 10,000 lines long then you would probably want everything
to be easier to find. Scalability wise we would never want everything to be in the same composable since in the long run
it makes expanding or editing existing code more complicated and prone to breaking.
 */

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {

    //State variables for form fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //State variables for errors
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) } //Will be used later for email errors
    var passwordError by remember { mutableStateOf(false) }
    var validEmailBool by remember { mutableStateOf(true) } //Will be used later for email errors

    var passwordVisible by remember { mutableStateOf(false) }

    //Hard coded variables for email and password
    val validEmail = "student@wccnet.edu"
    val validPassword = "password123"


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Student Login",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column (
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                //Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Full Name")},
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Person Icon")
                    },
                    isError = nameError,
                    supportingText = {
                        if(nameError) {
                            Text("Name cannot be empty")
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), //<-- Cool UI feature for keyboard that brings up Next button on keyboard
                    modifier = Modifier.fillMaxWidth()
                )

                //Email input
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false //will check for email errors
                        validEmailBool = true //will check if email is correct!

                        // This is what will set off our real time validator for the email error message
                        if(email.isNotBlank() && !isValidEmail(email)) {
                            emailError = true
                        }
                    },
                    label = {Text("Email")},
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email icon")
                    },
                    isError = emailError || !validEmailBool,
                    supportingText = {
                        when {
                            emailError -> // Will check regex
                                Text("Please enter a valid email")
                            !validEmailBool -> // Will check if correct
                                Text("Please enter the correct email")
                        }
                    },

                    /*
                    4. Above uses two types of logic. One where it checks for real time validation using the
                    emailError. It uses the onValueChange to reset the emailError to false which will then trigger our when
                    statement to check if emailError is true AND if the email doesn't match the regex pattern then we get
                    an error

                    The second type of logic is our correctness validator which will take our ValidEmailBool
                    and check if our email matches the correct one in order to login after all parameters for
                    making sure the email is valid has been passed. This will only occur after we hit the login button
                    and the check to make sure the email is the correct one fails, this is programmed in the button.


                    I tried this originally before asking AI for help:

                    isError = passwordError || !validPassword,
                        supportingText = {
                         when {
                        passwordError && password.isNotBlank() ->
                        Text("Password is too short")
                        !validPassword && password.length < 8 ->
                         Text("Password must be at least 8 characters")
                            !validPassword ->
                             Text("Invalid password")
                           }
                         }

                         The issue with this though is apparently the logic would conflict when
                         passwordError is true and the password is blank, the first condition being
                         isNotBlank would cause it to fall through letting errors slip
                     */
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    label = {Text("Password")},
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Lock icon")
                    },
                    trailingIcon = {
                        TextButton(onClick = {passwordVisible = !passwordVisible}) {
                            Text(if (passwordVisible) "Hide" else "Show")
                        }
                    },
                    visualTransformation = if(passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    isError = passwordError,
                    supportingText = {
                        if (passwordError) {
                            Text("Please enter the correct password")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                //Login Button Below

                Button(
                    onClick = {
                        //Resets the errors
                        nameError = false
                        emailError = false
                        validEmailBool = true
                        passwordError = false

                        //These will validate the information as we hit log in vvv

                        //Validate name
                        if (name.trim().isEmpty()) {
                            nameError = true
                        }

                        //Validate email syntax to make sure it is correct and valid
                        if (email.trim().isEmpty()) {
                            emailError = true
                        } else if (!isValidEmail(email)) { //Actually makes sure the email was the correct one
                            emailError = true
                        } else if (email.trim() != validEmail) {
                            validEmailBool = false
                        }

                        //Validate password
                        if (password.isEmpty()) {
                            passwordError = true
                        } else if (password != validPassword) {
                            passwordError = true
                        }

                        if (nameError || emailError || !validEmailBool || passwordError) {
                            return@Button // All will give the button
                        }

                        //All validations pass
                        onLoginSuccess(name.trim())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                }
            }
        }

    }



/**
 * Welcome screen that displays after successful login
 */
@Composable
fun WelcomeScreen(
    userName: String,
    onViewProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hello $userName!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onViewProfile,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("View Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Logout")
        }

    }
}

/**
 * Profile screen showing user information
 */
@Composable
fun ProfileScreen(
    userName: String,
    onBackToWelcome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //This will be our header
        Text(
            text = "User Profile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 22.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {


            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileRow(label = "Name", value = userName)
                ProfileRow(label = "Email", value = "student@college.edu")
                ProfileRow(label = "Student ID", value = "2024001")
                ProfileRow(label = "Major", value = "Computer Science")
                ProfileRow(label = "Year", value = "Freshman")
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBackToWelcome,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 24.dp)
        ) {
            Text("Back to welcome screen")
        }
    }
}

/**
 * Helper composable for profile information rows
 */
@Composable
fun ProfileRow(label: String, value: String) {

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value, //Displays values once we login
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Email validation function using regex (Chapter 7: Regular Expressions)
 */
fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return email.matches(emailPattern.toRegex())

}

/**
 * Preview function for the login screen
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLoginSuccess = {})
    }
}

/**
 * Preview function for the welcome screen
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            userName = "John Doe",
            onViewProfile = {},
            onLogout = {}
        )
    }
}

/**
 * Preview function for the profile screen
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            userName = "John Doe",
            onBackToWelcome = {}
        )
    }
}

/*
5. Scenarios where you would want to implement a top and bottom bar could vary but some popular reasons would be
to have banners at the top of the app for the title or a search bar, possibly a little window controller
on the bottom that let's you choose different app screens and etc. The benefits it offers are circumstantial
however do leave room for scalability if you want to add more features to an app while keeping the UI nice
and clean.
 */
