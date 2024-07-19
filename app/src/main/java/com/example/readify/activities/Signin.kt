// Signin.kt
package com.example.readify.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ImageButton
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnTogglePasswordVisibility: ImageButton

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "AUTH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        textViewForgotPassword = findViewById(R.id.txt_forgot_password)
        editTextEmail = findViewById(R.id.et_email)
        editTextPassword = findViewById(R.id.et_password)
        btnTogglePasswordVisibility = findViewById(R.id.btn_toggle_password_visibility)

        btnTogglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }
        toggleButtonRememberMe = findViewById(R.id.toggle_button_remember_me)
        signInInputsArray = arrayOf(editTextEmail, editTextPassword)

        sharedPreferences = getSharedPreferences("ReadifyPrefs", MODE_PRIVATE)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("27940962271-dfjq63k1ceh2b6s4ntoii5pptar1feir.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()

        findViewById<TextView>(R.id.btn_login_google).setOnClickListener {
            signInWithGoogle()
        }

        if (isGoogleAccountSaved()) {
            val googleAccountId = sharedPreferences.getString("googleAccountId", "")
            val credential = GoogleAuthProvider.getCredential(googleAccountId, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    } else {
                        toast("Sign-in failed, try again later.")
                        Log.d("Google sign in failed:",""+task.exception?.message)
                    }
                }
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

    private fun togglePasswordVisibility() {
        if (editTextPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editTextPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
        } else {
            editTextPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
        }
        editTextPassword.text?.let { editTextPassword.setSelection(it.length) } // Move cursor to end
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

    private fun saveGoogleLoginState(account: GoogleSignInAccount) {
        val editor = sharedPreferences.edit()
        editor.putString("googleAccountId", account.id)
        editor.apply()
    }

    private fun saveLoginState() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isRemembered", toggleButtonRememberMe.isChecked)
        editor.apply()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                } else {
                    Log.w(TAG, "Account is NULL")
                    toast("Sign-in failed, try again later.")
                }
            } catch (e: ApiException) {
                toast("Google sign in failed")
                Log.d("Google sign in failed:",""+e.message)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:${account.id}")
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveGoogleLoginState(account)
                    saveLoginState()
                    startActivity(Intent(this, Home::class.java))
                    toast("Signed in with Google successfully")
                    finish()
                } else {
                    toast("Sign-in failed, try again later.")
                    Log.d("Google sign in failed:",""+task.exception?.message)
                }
            }
    }

    private fun isGoogleAccountSaved(): Boolean {
        return sharedPreferences.getString("googleAccountId", "") != ""
    }
}
