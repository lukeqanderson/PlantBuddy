package com.example.plantbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity() {

    // creates var for Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // initializes the firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // grabs signIn and signUp buttons by id
        val signInButton = findViewById<Button>(R.id.sign_in)
        val signUpButton = findViewById<Button>(R.id.sign_up)

        // grabs email and password
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        // Intent to go back to plant list activity
        val startSignUp = Intent(this,SignUp::class.java)

        // Intent to go to main activity
        val startPlantList = Intent(this,MainActivity::class.java)

        // sets to go back to plant list activity on click
        signUpButton.setOnClickListener {
            startActivity(startSignUp)
        }

        // sets lister to sign in
        signInButton.setOnClickListener {
            // sets values to string for processing
            val emailString : String = email.text.toString()
            val passwordString : String = password.text.toString()

            // checks for empty fields
            if (emailString.isNotEmpty() && passwordString.isNotEmpty()) {
                //creates new user in firebase
                firebaseAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener {
                    // verifies success and sends user to login page
                    if (it.isSuccessful) {
                        startPlantList.putExtra("uid", firebaseAuth.currentUser?.uid)
                        // sends user to plant list on successful sign in
                        startActivity(startPlantList)
                    }
                    else {
                        // informs user of authentication failure
                        Toast.makeText(this@SignIn,"Incorrect username or password.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else {
                // sends user toast to inform them of error
                Toast.makeText(this@SignIn,"Details can not be empty.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}