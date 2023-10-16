package com.example.calorieguide;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    Button btn_logout, btn_deleteUser;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    View overlay;
    String uID, email;
    Boolean userDeleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        user = mainActivity.user;
        uID = mainActivity.uIDDB;
        email = mainActivity.email;

        overlay = view.findViewById(R.id.overlay);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_deleteUser = view.findViewById(R.id.btn_deleteUser);
        btn_logoutListener();
        btn_changePasswordListener();
        btn_deleteUserListener();

        return view;
    }

    private void btn_deleteUserListener() {
        btn_deleteUser.setOnClickListener(v -> {
            showDeleteDialog();
        });
    }

    private void showDeleteDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_user, null);

        final ImageButton closeDialog  = view.findViewById(R.id.close_DeleteDialog);
        Button goBack = view.findViewById(R.id.btn_declineDeleteAccount);
        Button deleteUser = view.findViewById(R.id.btn_AgreeDeleteAccount);

        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        overlay.setVisibility(View.VISIBLE);

        closeDialog.setOnClickListener(v -> alertDialog.dismiss());
        goBack.setOnClickListener(v -> alertDialog.dismiss());
        deleteUser.setOnClickListener(v -> showEnterPasswordDialog());


        alertDialog.setOnDismissListener(v -> overlay.setVisibility(View.GONE));
        alertDialog.show();
    }

    private void showEnterPasswordDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_enter_password, null);

        final ImageButton closeDialog  = view.findViewById(R.id.close_DeleteDialog);
        Button deleteAccount = view.findViewById(R.id.btn_deleteAccountWithPassword);

        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        overlay.setVisibility(View.VISIBLE);

        closeDialog.setOnClickListener(v -> alertDialog.dismiss());
        deleteAccount.setOnClickListener(v -> {
            EditText input = view.findViewById(R.id.input_password);
            String password = input.getText().toString();
            reauthUser(password);
        });


        alertDialog.setOnDismissListener(v -> overlay.setVisibility(View.GONE));
        alertDialog.show();
    }

    private void reauthUser(String password) {
        // Delete from Firebase
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            deleteUser();
                        } else {
                            Toast.makeText(getContext(), "You entered wrong password", Toast.LENGTH_SHORT).show();
                            Log.d("Firebase", "User Reauthentification failed", task.getException());
                        }
                    });
        }


    }

    private void deleteUser() {
        // Delete user from Firebase
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // User account deleted.
                            Log.d("Firebase", "User Deleted");
                            // Delete from Firestore
                            DocumentReference userRef = db.collection("users").document(uID);
                            userRef.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "User Deleted");
                                        // Show a toast message indicating success
                                        Toast.makeText(getContext(), "User Deleted", Toast.LENGTH_SHORT).show();
                                        // Send to Log In Page
                                        Intent intent = new Intent(getContext(), Login.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("Firestore", "User Deletion failed", e);
                                        // Handle Firestore deletion failure
                                    });
                        } else {
                            Log.d("Firebase", "User Deletion failed", task.getException());
                            // Handle Firebase deletion failure
                        }
                    }
                });
    }

    private void btn_changePasswordListener() {
        // Change Password
    }

    private void btn_logoutListener() {
        // Set a click listener for the button
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the logout action
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), Login.class); // Use requireContext() to get the context
                startActivity(intent);
                requireActivity().finish(); // Finish the hosting activity
            }
        });
    }
}