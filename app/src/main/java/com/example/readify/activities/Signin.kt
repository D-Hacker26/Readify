package com.example.readify.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.readify.R
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class Signin : AppCompatActivity() {
    private lateinit var signInEmail: String
    private lateinit var signInPassword: String
    private lateinit var signInInputsArray: Array<AppCompatEditText>
    private lateinit var textViewForgotPassword: TextView
    private lateinit var editTextEmail: AppCompatEditText
    private lateinit var editTextPassword: AppCompatEditText
    private lateinit var toggleButtonRememberMe: ToggleButton
    private lateinit var sharedPreferences: SharedPreferences
  //  private lateinit var googleSignInClient: GoogleSignInOptions
    private val RC_SIGN_IN = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        textViewForgotPassword = findViewById(R.id.txt_forgot_password)
        editTextEmail = findViewById(R.id.et_email)
        editTextPassword = findViewById(R.id.et_password)
        toggleButtonRememberMe = findViewById(R.id.toggle_button_remember_me)
        signInInputsArray = arrayOf(editTextEmail, editTextPassword)

        sharedPreferences = getSharedPreferences("ReadifyPrefs", MODE_PRIVATE)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

     //   googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<TextView>(R.id.btn_login_google).setOnClickListener {
            signInWithGoogle()
        }


        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        val textViewSignup: TextView = findViewById(R.id.txt_sign_up)
        textViewSignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        val buttonSignIn: AppCompatButton = findViewById(R.id.btn_sign_in)
        buttonSignIn.setOnClickListener {
            signInUser()
        }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        signInEmail = editTextEmail.text.toString().trim()
        signInPassword = editTextPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        saveLoginState()
                        startActivity(Intent(this, Home::class.java))
                        toast("Signed in successfully")
                        finish()
                    } else {
                        toast("Sign in failed")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }

    private fun signInWithGoogle() {
     //   val signInIntent = googleSignInClient.signInIntent
     //   startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                toast("Google sign in failed: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveLoginState()
                    startActivity(Intent(this, Home::class.java))
                    toast("Signed in with Google successfully")
                    finish()
                } else {
                    toast("Google sign in failed")
                }
            }
    }

    private fun saveLoginState() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isRemembered", toggleButtonRememberMe.isChecked)
        editor.apply()
    }
}
