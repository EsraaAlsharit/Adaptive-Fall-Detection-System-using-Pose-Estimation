package com.mosaza.falldetectionapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mosaza.falldetectionapp.Classes.User;
import com.mosaza.falldetectionapp.Other.CustomFirebaseMessagingService;
import com.mosaza.falldetectionapp.R;

public class SignUpActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 123;
    public static final String LAT_KEY = "LAT_KEY", LON_KEY = "LON_KEY";

    private EditText editTextUsername, editTextFirstName, editTextLastName, editTextEmail,
        editTextPassword, editTextPhone, editTextServerIPAddress;
    private Button buttonEnterLocation, buttonSignUp;

    private double latitude = 0f, longitude = 0f;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);

        editTextUsername = findViewById(R.id.sign_up_username_edit_text);
        editTextFirstName = findViewById(R.id.sign_up_first_name_edit_text);
        editTextLastName = findViewById(R.id.sign_up_last_name_edit_text);
        editTextEmail = findViewById(R.id.sign_up_email_edit_text);
        editTextPassword = findViewById(R.id.sign_up_password_edit_text);
        editTextPhone = findViewById(R.id.sign_up_phone_edit_text);
        editTextServerIPAddress = findViewById(R.id.sign_up_server_ip_edit_text);
        buttonEnterLocation = findViewById(R.id.sign_up_enter_location_button);
        buttonSignUp = findViewById(R.id.sign_up_sign_up_button);

        buttonEnterLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValues()){
                    createAccount();
                }
            }
        });
    }

    private boolean checkValues(){
        if(editTextUsername.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter Username", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editTextFirstName.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter First Name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editTextLastName.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter Last Name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editTextEmail.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter Email", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editTextPassword.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter Password", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editTextPhone.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter Phone", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editTextServerIPAddress.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter Server IP Address", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!checkIPAddress(editTextServerIPAddress.getText().toString())){
            Toast.makeText(this, "Invalid Server IP Address", Toast.LENGTH_LONG).show();
            return false;
        }
        if(latitude == 0f && longitude == 0f ){
            Toast.makeText(this, "You must enter Location", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean checkIPAddress(String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra(LAT_KEY, 0f);
            longitude = data.getDoubleExtra(LON_KEY, 0f);
        }
    }

    private void createAccount(){
        progressDialog.setTitle("Signing up...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            saveOnDatabase();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveOnDatabase() {
        final User user = new User(
                FirebaseAuth.getInstance().getUid(),
                editTextUsername.getText().toString(),
                editTextFirstName.getText().toString(),
                editTextLastName.getText().toString(),
                editTextEmail.getText().toString(),
                editTextPhone.getText().toString(),
                editTextServerIPAddress.getText().toString(),
                latitude,
                longitude);

        FirebaseFirestore.getInstance()
                .collection("User")
                .document(FirebaseAuth.getInstance().getUid())
                .set(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            intent.putExtra("UserDetails", user);
                            startActivity(intent);
                            finishAffinity();
                        }else{
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}