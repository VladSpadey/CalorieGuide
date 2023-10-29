package com.example.calorieguide.Utils;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.calorieguide.GetAdditionalInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.calorieguide.Utils.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class dbManager {
    private FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    private Map<String, Object>  userDataList;

    public dbManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user != null){
            setupFirestoreListener();
        }
    }

    private void setupFirestoreListener() {
        String uid = user.getUid();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("dbManager", "1. DocumentSnapshot data: " + document.getData());
                    Map<String, Object> userDataList = document.getData();
                    Log.d("dbManager", "3. UserData: " + userDataList);

                } else {
                    Log.d("dbManager", "No such document");
                }
            } else {
                Log.d("dbManager", "get failed with ", task.getException());
            }
        });
    }

        public Map<String, Object>  getUserData() {
            return userDataList;
        }
}
