package com.redsystemstudio.appcomprayventa


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.redsystemstudio.appcomprayventa.Opciones_login.Login_email


class AdminActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar el fragment inicial
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminHomeFragment())
                .commitNow()
        }

        // Botón de cerrar sesión
        findViewById<Button>(R.id.button_logout).setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, Login_email::class.java))
            finish()
        }
    }
}
