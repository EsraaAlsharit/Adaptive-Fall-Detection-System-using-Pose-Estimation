package com.mosaza.falldetectionapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mosaza.falldetectionapp.Classes.User;
import com.mosaza.falldetectionapp.Other.CustomFirebaseMessagingService;
import com.mosaza.falldetectionapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        editTextEmail = findViewById(R.id.log_in_email_edit_text);
        editTextPassword = findViewById(R.id.log_in_password_edit_text);
        buttonLogin = findViewById(R.id.log_in_log_in_button);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValues()){
                    signIn();
                }
            }
        });
    }

    private boolean checkValues() {
        if (editTextEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter Email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter Password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void signIn() {
        progressDialog.setTitle("Logging In...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                editTextEmail.getText().toString(),
                editTextPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.setTitle("Getting Your Data...");
                            getUserData();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getUserData() {
        FirebaseFirestore.getInstance()
                .collection("User")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            DocumentSnapshot Document = task.getResult();
                            if(Document.exists()){
                                User user = new User();
                                user.documentToObject(Document);
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("UserDetails", user);
                                startActivity(intent);
                                finishAffinity();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Wrong Input", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}