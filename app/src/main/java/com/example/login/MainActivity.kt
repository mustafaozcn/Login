package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.login.ui.theme.LoginTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.SignInButton

class MainActivity : ComponentActivity() {

    // GoogleSignInClient ve FirebaseAuth nesneleri
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // GoogleSignInOptions yapılandırması
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("137667088449-db908gs3a0dn21blna6iolc9r85i4qno.apps.googleusercontent.com") // Firebase'den aldığınız client_id
            .requestEmail()
            .build()

        // GoogleSignInClient oluşturma
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // activity_main.xml dosyasını set et
        setContentView(R.layout.activity_main)

        // Google Sign-In butonunu XML üzerinden buluyoruz
        val signInButton: SignInButton = findViewById(R.id.sign_in_button)

        // Butona tıklama işlemi ekliyoruz
        signInButton.setOnClickListener {
            signInWithGoogle() // Google ile giriş işlemini başlat
        }
    }

    // Google ile giriş işlemini başlatma
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN) // RC_SIGN_IN sabitini kullan
    }

    // Giriş işlemi tamamlandığında yapılacak işlemler
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Hata durumunda yapılacak işlemler
            }
        }
    }

    // Firebase ile Google kullanıcısını doğrulama
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Giriş başarılı olduğunda yapılacak işlemler
                } else { 
                    // Hata durumunda yapılacak işlemler
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001 // Google Sign-In için requestCode
    }
}
