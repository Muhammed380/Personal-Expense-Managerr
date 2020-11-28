package com.muhammed.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.sentry.Sentry;

public class MainActivity extends AppCompatActivity {
//https://www.youtube.com/watch?v=P_54KPvfwIE&list=PLRxuR_G2-IzXkpWKSzLIxRjn869qUr8Zr&index=1
// 37
//https://medium.com/better-programming/33-amazing-github-repositories-for-mobile-developers-c62cd8ea0e98
    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;
    private TextView mForgetPassword;
    private TextView mSignupHere;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Sentry.captureMessage("testing SDK setup");


        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!= null) {
            startActivity(new Intent(this,HomeActivity.class));
        }
        loginDetails();
    }
    private void loginDetails () {

        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mForgetPassword = findViewById(R.id.forget_password);
        mSignupHere = findViewById(R.id.sigin_here);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail .setError("Email  Required...");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                        mPass.setError("Password Required...");
                        return;
                }
                mDialog.setMessage("Processing...");
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            Toast.makeText(getApplicationContext(),"Login Succesfull...", Toast.LENGTH_SHORT).show();
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Login Failed...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

        mSignupHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity2.class));
            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResetActivity.class));
            }
        });
    }


    }
