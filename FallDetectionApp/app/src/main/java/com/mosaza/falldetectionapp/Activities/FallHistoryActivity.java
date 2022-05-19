package com.mosaza.falldetectionapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mosaza.falldetectionapp.Classes.Fall;
import com.mosaza.falldetectionapp.Other.FallHistoryRecyclerAdapter;
import com.mosaza.falldetectionapp.R;

import java.util.ArrayList;
import java.util.List;

public class FallHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Fall> fallList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_history);

        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.fall_history_recycler_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFallHistory();
    }

    private void getFallHistory(){
        fallList = new ArrayList<>();

        progressDialog.setTitle("Getting Fall History...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseFirestore
                .getInstance()
                .collection("Fall")
                .whereEqualTo("userID", FirebaseAuth.getInstance().getUid())
                .orderBy("fallDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Fall fall = new Fall();
                                    fall.documentToObject(document);
                                    fallList.add(fall);
                                }
                                displayList();
                            }
                            else
                                Toast.makeText(FallHistoryActivity.this, "No Falls Found", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(FallHistoryActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void displayList() {
        FallHistoryRecyclerAdapter adapter = new FallHistoryRecyclerAdapter(fallList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}