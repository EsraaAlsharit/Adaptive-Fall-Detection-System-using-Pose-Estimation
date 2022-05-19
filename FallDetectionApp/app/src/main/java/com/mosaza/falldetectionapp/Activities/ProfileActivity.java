package com.mosaza.falldetectionapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mosaza.falldetectionapp.Classes.User;
import com.mosaza.falldetectionapp.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewID, textViewEmail, textViewUsername, textViewFirstName, textViewLastName, textViewPhone, textViewServerIP;
    private ImageButton buttonCopyID, buttonUserName, buttonFirstName, buttonLastName, buttonPhone, buttonServerIP;
    private Button buttonEditLocation;

    private double latitude = 0f, longitude = 0f;
    private double oldLat = 33.3f, oldLon = 33.3f;

    private User user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressDialog = new ProgressDialog(this);
        user = getIntent().getParcelableExtra("UserDetails");

        setViews();
    }

    private void setViews() {
        textViewID = findViewById(R.id.profile_id_text);
        textViewEmail = findViewById(R.id.profile_email_text);
        textViewUsername = findViewById(R.id.profile_username_text);
        textViewFirstName = findViewById(R.id.profile_first_name_text);
        textViewLastName = findViewById(R.id.profile_last_name_text);
        textViewPhone = findViewById(R.id.profile_phone_text);
        textViewServerIP = findViewById(R.id.profile_server_ip_text);

        buttonCopyID = findViewById(R.id.profile_id_button);
        buttonUserName = findViewById(R.id.profile_username_button);
        buttonFirstName = findViewById(R.id.profile_first_name_button);
        buttonLastName = findViewById(R.id.profile_last_name_button);
        buttonPhone = findViewById(R.id.profile_phone_button);
        buttonServerIP = findViewById(R.id.profile_server_ip_button);

        buttonEditLocation = findViewById(R.id.profile_enter_location_button);

        buttonCopyID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("User ID", textViewID.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ProfileActivity.this, "ID Copied to Clipboard", Toast.LENGTH_LONG).show();
            }
        });

        buttonUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Username", textViewUsername.getText().toString(), InputType.TYPE_CLASS_TEXT);
            }
        });

        buttonFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("First Name", textViewFirstName.getText().toString(), InputType.TYPE_CLASS_TEXT);
            }
        });

        buttonLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Last Name", textViewLastName.getText().toString(), InputType.TYPE_CLASS_TEXT);
            }
        });

        buttonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Phone", textViewPhone.getText().toString(), InputType.TYPE_CLASS_PHONE);
            }
        });

        buttonServerIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Server IP", textViewServerIP.getText().toString(), InputType.TYPE_CLASS_PHONE);
            }
        });

        buttonEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                intent.putExtra(SignUpActivity.LAT_KEY, latitude);
                intent.putExtra(SignUpActivity.LON_KEY, longitude);
                startActivityForResult(intent, SignUpActivity.REQUEST_CODE);
            }
        });

        setData();
    }

    private void setData() {
        textViewID.setText(user.getId());
        textViewEmail.setText(user.getEmail());
        textViewUsername.setText(user.getUsername());
        textViewFirstName.setText(user.getFirstName());
        textViewLastName.setText(user.getLastName());
        textViewPhone.setText(user.getPhone());
        textViewServerIP.setText(user.getServerIPAddress());
        latitude = user.getLatitude();
        longitude = user.getLongitude();
    }

    private void showEditDialog(final String title, String oldValue, int inputType){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final TextView dialogTitle = dialog.findViewById(R.id.dialog_edit_title);
        final EditText dialogValue = dialog.findViewById(R.id.dialog_edit_value);
        final Button dialogSave = dialog.findViewById(R.id.dialog_edit_save);
        final Button dialogCancel = dialog.findViewById(R.id.dialog_edit_cancel);

        dialogTitle.setText(("Enter new " + title + " value"));
        dialogValue.setHint(oldValue);
        dialogValue.setInputType(inputType);

        dialog.show();

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogValue.getText().toString().isEmpty())
                    Toast.makeText(ProfileActivity.this, "You must enter a value", Toast.LENGTH_LONG).show();
                else {
                    switch (title) {
                        case "Username":
                            textViewUsername.setText(dialogValue.getText().toString());
                            user.setUsername(dialogValue.getText().toString());
                            updateOnDatabase("username", user.getUsername(), null, null);
                            break;
                        case "First Name":
                            textViewFirstName.setText(dialogValue.getText().toString());
                            user.setFirstName(dialogValue.getText().toString());
                            updateOnDatabase("firstName", user.getFirstName(), null, null);
                            break;
                        case "Last Name":
                            textViewLastName.setText(dialogValue.getText().toString());
                            user.setLastName(dialogValue.getText().toString());
                            updateOnDatabase("lastName", user.getLastName(), null, null);
                            break;
                        case "Phone":
                            textViewPhone.setText(dialogValue.getText().toString());
                            user.setPhone(dialogValue.getText().toString());
                            updateOnDatabase("phone", user.getPhone(), null, null);
                            break;
                        case "Server IP":
                            if (!checkIPAddress(dialogValue.getText().toString()))
                                Toast.makeText(ProfileActivity.this, "Invalid Server IP Address", Toast.LENGTH_LONG).show();
                            else {
                                textViewServerIP.setText(dialogValue.getText().toString());
                                user.setServerIPAddress(dialogValue.getText().toString());
                                updateOnDatabase("serverIPAddress", user.getServerIPAddress(), null, null);
                            }
                            break;
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    private boolean checkIPAddress(String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignUpActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra(SignUpActivity.LAT_KEY, oldLat);
            longitude = data.getDoubleExtra(SignUpActivity.LON_KEY, oldLon);
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            updateOnDatabase("latitude", latitude, "longitude", longitude);
        }
    }

    private void updateOnDatabase(String FieldName, Object FieldData, String secondField, Object secondValue){
        progressDialog.setTitle("Updating Your Data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(secondField != null && secondValue != null){
            FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(FirebaseAuth.getInstance().getUid())
                    .update(FieldName, FieldData,
                            secondField, secondValue)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Data Updated Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(FirebaseAuth.getInstance().getUid())
                    .update(FieldName, FieldData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Data Updated Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("UserDetails", user);
        setResult(RESULT_OK, intent);
        finish();
    }

}