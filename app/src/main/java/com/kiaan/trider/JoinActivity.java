//The class sets the activity_join GUI and allows a user to join a group by inputing the name and code of the group. It then adds the groupName to the User's GorupList and the User into the GroupMember list in the database
//Kiaan Upamanyu

package com.kiaan.trider;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import ObjectClasses.GroupObjectClass;
import ObjectClasses.User;

public class JoinActivity extends AppCompatActivity {

    private GroupObjectClass group = new GroupObjectClass();
    private String code;
    private Button Join;
    private EditText GroupName;
    private EditText GroupCode;
    private User current;
    private boolean local;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Join = findViewById(R.id.JoinGroupButton);
        GroupName = findViewById(R.id.GroupNameEditText);
        GroupCode = findViewById(R.id.GroupCodeEditText);
        current = new User();
        local = false;

        db = FirebaseFirestore.getInstance();
        db.collection("USERS").document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                current = documentSnapshot.toObject(User.class);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
                startActivity(intent);
            }
        };

    }
    public void JoinGroupFunc(View view) {
        db.collection("GROUPS").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e("ERROR", "onEvent: ListenFailed", e);
                    Toast.makeText(getApplicationContext(), "UNABLE TO JOIN GROUP...PLEASE CHECK DETAILS", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Toast.makeText(getApplicationContext(), "UNABLE TO JOIN GROUP...PLEASE CHECK DETAILS", Toast.LENGTH_SHORT).show();
                        }

                        else if (GroupName.getText().toString().equals(doc.toObject(GroupObjectClass.class).getGroupName()) && GroupCode.getText().toString().equals(doc.toObject(GroupObjectClass.class).getGroupCode())) {
                            db.collection("GROUPS")
                                    .document(doc.toObject(GroupObjectClass.class).getGroupName())
                                    .collection("GROUPMEMBERS")
                                    .document(current.getUserID()).set(current).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        db.collection("USERS")
                                                .document(current.getUserID())
                                                .collection("GROUPS")
                                                .document(doc.toObject(GroupObjectClass.class).getGroupName())
                                                .set(doc.toObject(GroupObjectClass.class))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(), "JOINED GROUP SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                                            GroupName.setText("");
                                                            GroupCode.setText("");
                                                            local = true;
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });

    }
}