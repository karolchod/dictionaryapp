package com.dictionaryapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val et_email: TextInputEditText = findViewById(R.id.etforgotemail)


        val forgotButton: Button = findViewById(R.id.ForgotButton)
        forgotButton.setOnClickListener{
            when{
                TextUtils.isEmpty(et_email.text.toString().trim {it <= ' '}) ->{
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = et_email.text.toString().trim {it <= ' '}

                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener{task ->
                            if(task.isSuccessful){
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Reset link sent successfully to your email.",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }else{
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }



                        }

                }
            }
        }


        val loginTV: TextView = findViewById(R.id.loginTextView)
        loginTV.setOnClickListener{
            onBackPressed()
        }



    }
}