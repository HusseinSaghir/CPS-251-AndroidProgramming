package com.example.contactinfoentry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ContactValidatorApp()
            }
        }
    }
}

@Composable
fun ContactValidatorApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contact Information",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ContactForm()
    }
}

@Composable
fun ContactForm() {

    //State variables
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }

    //Submit info
    var submittedInformation by remember { mutableStateOf("") }

    //Validation State variables
    var isNameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPhoneValid by remember { mutableStateOf(true) }
    var isZipCodeValid by remember { mutableStateOf(true) }

/*
1. Above is our ContactForm Function where we declared our variables.
These needed to be separated in order to do 2 things. The state variable
will will capture the data of whats entered like the name and the validator
will make sure all parameters are met and whether or not to present
an error message.
 */





    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp) //Added this
            )
            .padding(
                horizontal = 16.dp,
                vertical = 20.dp
    ),

    verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        //Below will make sure text fields are not blank and filled
        //with the appropriate values

        NameField(
            name = name,
            isNameValid = isNameValid,
            onValueChange = { newName -> name = newName
            isNameValid = newName.isNotBlank()
            }
        )

        //Below will validate if info is correct

       EmailField(
           email = email,
           isEmailValid = isEmailValid,
           onValueChange = { newEmail -> email = newEmail
           isEmailValid = validateEmail(newEmail)
           }
       )

        PhoneField(
          phone = phone,
            isPhoneValid = isPhoneValid,
            onValueChange = { newPhone ->
                phone = newPhone
                isPhoneValid = validatePhone(newPhone)
            }
        )

      ZipCodeField(
          zipCode = zipCode,
          isZipValid = isZipCodeValid,
          onValueChange = { newZipCode ->
              zipCode = newZipCode
              isZipCodeValid = validateZipCode(newZipCode)
          }
      )


       /*
       4. Below is our Submit button with our enable conditions. This simply ensures that all our fields
       will have content by checking is not blank and check if all field meet our conditions
       with is etc valid. Without these the user would just type whatever they want and be able to submit
       the form.
        */

        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() &&
                    phone.isNotBlank() && zipCode.isNotBlank() &&
                    isNameValid && isEmailValid && isPhoneValid && isZipCodeValid) {
                    submittedInformation = //This part will be important for the card
                        "Name: $name\nEmail: $email\nPhone: $phone \nZip: $zipCode"
                }
            },

                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && email.isNotBlank() &&
                        phone.isNotBlank() && zipCode.isNotBlank() && isNameValid
                        && isEmailValid && isPhoneValid && isZipCodeValid
        ) {
            Text("Submit")
        }

        /*
        5. Below If the conditions
        are all met then it will produce a card that updates with our submitted information.
        This concept is called Conditional Rendering!
         */

        if(submittedInformation.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = submittedInformation,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/*
2. Below we have all of our input fields for name, email, etc.
If we look at the NameField as an easy example you will see the "isError" label.
How this work is it will activate once our name field is not blank or has begun
having something entered, then the part after the && will mark the name as
invalid until parameters are met. This is done so error messages do not appear
before we even start typing.
 */
@Composable
fun NameField(
    name: String,
    isNameValid: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onValueChange,
        label = { Text("Name")},
        modifier = Modifier.fillMaxWidth(),
        isError = name.isNotBlank() && !isNameValid,
        supportingText =  {
            if (name.isNotBlank() && !isNameValid) {
                Text("You have a Name and you will give it to me!!!")
            }
        }
    )
}

@Composable
fun EmailField(
    email: String,
    isEmailValid: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text("Email")},
        modifier = Modifier.fillMaxWidth(),
        isError = email.isNotBlank() && !isEmailValid,
        supportingText =  {
            if (email.isNotBlank() && !isEmailValid) {
                Text("It's 2025 and you don't have a proper email yet?!")
            }
        }
    )
}

@Composable
fun PhoneField(
    phone: String,
    isPhoneValid: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = phone,
        onValueChange = onValueChange,
        label = { Text("Phone Number")},
        modifier = Modifier.fillMaxWidth(),
        isError = phone.isNotBlank() && !isPhoneValid,
        supportingText =  {
            if (phone.isNotBlank() && !isPhoneValid) {
                Text("Formats Accepted: 123-456-7890 or 123/456/7890")
            }
        }
    )
}

@Composable
fun ZipCodeField(
    zipCode: String,
    isZipValid: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = zipCode,
        onValueChange = onValueChange,
        label = { Text("Zip Code")},
        modifier = Modifier.fillMaxWidth(),
        isError = zipCode.isNotBlank() && !isZipValid,
        supportingText =  {
            if (zipCode.isNotBlank() && !isZipValid) {
                Text("What's your area code kid?")
            }
        }
    )
}

/*
3. Below is our validateEmail function. How it works goes like this, we start the string with
"^" and end it with the "$". We begin by giving a parameter that gets all possible characters
from A to Z in upper and lower case and all digits between 0 and 9 including a few symbols.
we then do an addition symbol and do the @ symbol to symbolize what comes after the @ in our email,
then repeat the parameters. Lastly another addition symbol, then specify that there needs to be a dot and the parameters
that follow after that as well.

If we had it just check for @ and . then literally nonsense would be valid like an email
such as hussein@@@...com and that wouldn't be good
 */

//Using Regex
fun validateEmail(email: String): Boolean {
    if (email.isBlank()) return true
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return email.matches(emailRegex)
}

fun validatePhone(phone: String): Boolean {
    if (phone.isBlank()) return true
    val phoneRegex = "^\\d{3}[-/]\\d{3}[-/]\\d{4}$".toRegex()
    return phone.matches(phoneRegex)
}

fun validateZipCode(zipCode: String): Boolean {
    if (zipCode.isBlank()) return true
    val zipRegex = "^\\d{5}$".toRegex()
    return zipCode.matches(zipRegex)
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContactValidatorAppPreview() {
    ContactValidatorApp()
}