package com.redsystemstudio.appcomprayventa.Opciones_login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.redsystemstudio.appcomprayventa.AdminActivity
import com.redsystemstudio.appcomprayventa.MainActivity
import com.redsystemstudio.appcomprayventa.RecuperarPassword
import com.redsystemstudio.appcomprayventa.Registro_email
import com.redsystemstudio.appcomprayventa.databinding.ActivityLoginEmailBinding
import java.util.concurrent.TimeUnit

class Login_email : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog

    private var email = ""
    private var password = ""
    private var attempts = 0
    private var blockEndTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Usuarios")

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnIngresar.setOnClickListener {
            validarInfo()
        }

        binding.TxtRegistrarme.setOnClickListener {
            startActivity(Intent(this@Login_email, Registro_email::class.java))
        }

        binding.TvRecuperar.setOnClickListener {
            startActivity(Intent(this@Login_email, RecuperarPassword::class.java))
        }
    }

    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()
        } else if (email.isEmpty()) {
            binding.EtEmail.error = "Ingrese email"
            binding.EtEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.EtPassword.error = "Ingrese password"
            binding.EtPassword.requestFocus()
        } else {
            checkAccountStatus()
        }
    }

    private fun checkAccountStatus() {
        val userRef = database.child(email.replace(".", "_")) // Utiliza email como identificador

        userRef.get().addOnSuccessListener { snapshot ->
            attempts = snapshot.child("attempts").getValue(Int::class.java) ?: 0
            blockEndTime = snapshot.child("blockEndTime").getValue(Long::class.java) ?: 0L

            if (System.currentTimeMillis() < blockEndTime) {
                val remainingTime = blockEndTime - System.currentTimeMillis()
                Toast.makeText(
                    this, "Cuenta bloqueada. Intente nuevamente en ${TimeUnit.MILLISECONDS.toMinutes(remainingTime)} minutos.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                loginUsuario(userRef)
            }
        }.addOnFailureListener {
            loginUsuario(userRef) // Si no se puede obtener el estado, intentar iniciar sesión de todos modos
        }
    }

    private fun loginUsuario(userRef: DatabaseReference) {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                resetAttempts(userRef)
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    database.child(userId).get().addOnSuccessListener { snapshot ->
                        progressDialog.dismiss()
                        if (snapshot.exists()) {
                            val tipoUsuario = snapshot.child("tipoUsuario").value as? String
                            if (tipoUsuario != null) {
                                when (tipoUsuario) {
                                    "Administrador" -> {
                                        val intent = Intent(this, AdminActivity::class.java)
                                        startActivity(intent)
                                    }
                                    "Vendedor" -> {
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else -> {
                                        Toast.makeText(
                                            this,
                                            "Tipo de usuario desconocido",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                finishAffinity()
                                Toast.makeText(
                                    this,
                                    "Bienvenido(a)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error: tipoUsuario no encontrado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Error: documento de usuario no existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Error al obtener el tipo de usuario: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Error: userId es null",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                incrementAttempts(userRef)
                Toast.makeText(
                    this,
                    "No se pudo iniciar sesión debido a información incorrecta: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun resetAttempts(userRef: DatabaseReference) {
        attempts = 0
        blockEndTime = 0L
        userRef.child("attempts").setValue(attempts)
        userRef.child("blockEndTime").setValue(blockEndTime)
    }

    private fun incrementAttempts(userRef: DatabaseReference) {
        attempts++
        var blockDuration = 0L
        when (attempts) {
            3 -> blockDuration = TimeUnit.MINUTES.toMillis(2)
            6 -> blockDuration = TimeUnit.MINUTES.toMillis(10)
            9 -> {
                Toast.makeText(
                    this,
                    "Cuenta bloqueada. Contacte al administrador.",
                    Toast.LENGTH_SHORT
                ).show()
                blockDuration = Long.MAX_VALUE // Bloqueo indefinido
            }
        }

        if (blockDuration > 0) {
            blockEndTime = System.currentTimeMillis() + blockDuration
        }

        userRef.child("attempts").setValue(attempts)
        userRef.child("blockEndTime").setValue(blockEndTime)
    }
}
