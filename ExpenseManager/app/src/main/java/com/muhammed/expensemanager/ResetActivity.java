package com.muhammed.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    private TextView emailText;
    private Button sndButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        emailText = findViewById(R.id.editText_resetActivity);
        sndButton = findViewById(R.id.button_resetActivity);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

       sndButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String userEmail = emailText.getText().toString();

               if (TextUtils.isEmpty(userEmail)) {

                   Toast.makeText(ResetActivity.this,"Please write your valid email address first...",Toast.LENGTH_SHORT).show();
               }else {
                   mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           if (task.isSuccessful()) {

                               Toast.makeText(ResetActivity.this,"Please check your Email Account, If you " +
                                       "want to reset your password...",Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(ResetActivity.this,MainActivity.class));
                           }else {
                               String message = task.getException().getMessage();
                               Toast.makeText(ResetActivity.this,"Error Occured: " + message,Toast.LENGTH_SHORT).show();

                           }
                       }
                   });

               }
           }
       });


    }
}