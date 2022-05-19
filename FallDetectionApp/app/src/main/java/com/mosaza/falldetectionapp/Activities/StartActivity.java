package com.mosaza.falldetectionapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mosaza.falldetectionapp.Classes.User;
import com.mosaza.falldetectionapp.R;

public class StartActivity extends AppCompatActivity {

    private Button buttonLogin, buttonSignUp;
    private Intent intent;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        progressDialog = new ProgressDialog(this);

        buttonLogin = findViewById(R.id.start_login_button);
        buttonSignUp = findViewById(R.id.start_sign_up_button);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(StartActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            progressDialog.setTitle("Getting Your Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            getUserData();
        }
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
                                Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                                intent.putExtra("UserDetails", user);
                                startActivity(intent);
                                finishAffinity();
                            }
                            else{
                                Toast.makeText(StartActivity.this, "Wrong Input", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(StartActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}