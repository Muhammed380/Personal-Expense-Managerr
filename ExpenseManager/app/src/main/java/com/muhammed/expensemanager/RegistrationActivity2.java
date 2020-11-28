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

public class RegistrationActivity2 extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnReg;
    private TextView mSignin;

    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        registration();

    }

    private void registration() {

        mEmail = findViewById(R.id.email_reg);
        mPass = findViewById(R.id.password_reg);
        btnReg = findViewById(R.id.btn_reg);
        mSignin = findViewById(R.id.signup_reg);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {

                    mEmail.setError("Email Required...");

                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password Required...");
                }

                mDialog.setMessage("Processing...");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if  (btnReg == null && TextUtils.isEmpty(pass) && TextUtils.isEmpty(email)) {
                        }

                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Complete", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }else  {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Field...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}