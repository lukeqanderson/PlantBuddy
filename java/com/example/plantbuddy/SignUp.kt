package com.example.plantbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    // var for firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // var for firebase realtime database
    private lateinit var db : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // initializes the firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // grabs signIn and signUp buttons by id
        val signInButton = findViewById<Button>(R.id.sign_in)
        val signUpButton = findViewById<Button>(R.id.sign_up)

        // grabs user inputs
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val passwordConfirm = findViewById<EditText>(R.id.password_confirm)

        // Intent to go back to plant list activity
        val startSignIn = Intent(this,SignIn::class.java)

        // sets to go to sign in on click
        signInButton.setOnClickListener {
            startActivity(startSignIn)
        }

        // sets the sign up button on click for authentication
        signUpButton.setOnClickListener {
            // grabs data from email, password, and confirmation
            val emailString : String = email.text.toString()
            val passwordString : String = password.text.toString()
            val passwordConfirmString : String = passwordConfirm.text.toString()

            // checks for empty fields and equal password
            if (emailString.isNotEmpty()
                && passwordString.isNotEmpty()
                && passwordConfirmString.isNotEmpty()
                && passwordString == passwordConfirmString) {

                //creates new user in firebase
                firebaseAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener {
                    // verifies success and sends user to login page
                    if (it.isSuccessful) {
                        // creates a new user in the database
                        db = FirebaseDatabase.getInstance().getReference("Users")
                        // gets unique key from authenticator
                        val uid = firebaseAuth.currentUser?.uid
                        val user = User(emailString)
                        // verifies object was successfully added
                        if (uid != null) {
                            db.child(uid).setValue(user).addOnSuccessListener{
                                // sends user to sign in activity
                                startActivity(startSignIn)
                            }.addOnFailureListener {
                                // informs user of of firebase error
                                Toast.makeText(this@SignUp,"Failed to add user to database, please check with admin.",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else {
                        // informs user of of firebase error
                        Toast.makeText(this@SignUp,"Failed to create user in Firebase, please check with admin.",Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else {
                // sends user toast to inform them of error
                Toast.makeText(this@SignUp,"Details can not be empty and passwords must match.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}