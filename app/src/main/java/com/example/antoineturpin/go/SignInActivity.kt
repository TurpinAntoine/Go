package com.example.antoineturpin.go

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import java.util.Arrays.asList
import com.google.firebase.auth.FirebaseUser





class SignInActivity : AppCompatActivity() {



    private val RC_SIGN_IN = 123
    val auth = FirebaseAuth.getInstance()!!

    fun showSnackbar(id : Int){
        Snackbar.make(findViewById(R.id.sign_in_container), resources.getString(id), Snackbar.LENGTH_LONG).show()
    }

    var providers = Arrays.asList(
            AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
            )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        if(auth.currentUser != null){ //If user is signed in
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.AppTheme)      // Set theme
                            .build(),
                    RC_SIGN_IN);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                // Sign in failed, check response for error code
            }
        }
    }


}
