package com.mosaza.falldetectionapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mosaza.falldetectionapp.Classes.User;
import com.mosaza.falldetectionapp.Other.CustomFirebaseMessagingService;
import com.mosaza.falldetectionapp.R;

import static com.mosaza.falldetectionapp.Activities.SignUpActivity.REQUEST_CODE;

public class HomeActivity extends AppCompatActivity {

    private Button buttonLiveStreaming, buttonProfile, buttonFallHistory, buttonCallAmbulance, buttonLogout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = getIntent().getParcelableExtra("UserDetails");

        buttonLiveStreaming = findViewById(R.id.home_live_streaming_button);
        buttonProfile = findViewById(R.id.home_profile_button);
        buttonFallHistory = findViewById(R.id.home_fall_history_button);
        buttonCallAmbulance = findViewById(R.id.home_call_ambulance_button);
        buttonLogout = findViewById(R.id.home_logout_button);

        buttonLiveStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LiveStreamActivity.class);
                intent.putExtra("SERVER_IP", user.getServerIPAddress());
                startActivity(intent);
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("UserDetails", user);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        buttonFallHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FallHistoryActivity.class);
                startActivity(intent);
            }
        });

        buttonCallAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:911"));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            10);
                } else {
                    try {
                        startActivity(callIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to stop notifications
                updateUserToken(false);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        updateUserToken(true);
        requestSmsPermission();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            user = data.getParcelableExtra("UserDetails");
        }
    }

    private void updateUserToken(boolean add){
        String value;
        if(add)
            value = CustomFirebaseMessagingService.getToken(this);
        else
            value = "NONE";

        FirebaseFirestore
                .getInstance()
                .collection("User")
                .document(FirebaseAuth.getInstance().getUid())
                .update("token", value);
    }

    private void requestSmsPermission(){
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    10);
        }
    }
}