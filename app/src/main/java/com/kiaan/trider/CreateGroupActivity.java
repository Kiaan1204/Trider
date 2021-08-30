//This class is for the activity_create_group activity and allows the user to create a new group by inputing a GroupName for Identification and a GroupCode for other users to join.
// Kiaan Upamanyu


package com.kiaan.trider;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.security.acl.Group;

import ObjectClasses.GroupObjectClass;
import ObjectClasses.User;

public class CreateGroupActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText GroupName;
    private EditText GroupCode;
    User current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        db = FirebaseFirestore.getInstance();
        GroupName = findViewById(R.id.GroupNameEditText);
        GroupCode = findViewById(R.id.GroupCodeEditText);
        current = new User();


        //Sets the currently logged in user.
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

    //Called when the create button is pressed. Creates a new group, save it in the database and adds the user to the Member list of the group. It also add the group's name to the User's list of the groups he/she is part of."
    public void CreateGroupFunc(View view) {
        if(GroupName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "PLEASE FILL IN ALL FIELDS", Toast.LENGTH_SHORT).show();
        }
        db.collection("GROUPS").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots.isEmpty()){
                    if(GroupName.getText().toString() != null && GroupCode.getText().toString() != null){
                        GroupObjectClass newGroup = new GroupObjectClass();
                        newGroup.setGroupName(GroupName.getText().toString());
                        newGroup.setGroupCode(GroupCode.getText().toString());
                        db.collection("GROUPS")
                                .document(GroupName.getText().toString())
                                .set(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    db.collection("GROUPS")
                                            .document(GroupName.getText().toString())
                                            .collection("GROUPMEMBERS")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .set(current).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                db.collection("USERS")
                                                        .document(current.getUserID())
                                                        .collection("GROUPS")
                                                        .document(GroupName.getText().toString())
                                                        .set(newGroup)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "NEW GROUP CREATED", Toast.LENGTH_SHORT).show();
                                                                }
                                                                GroupName.setText("");
                                                                GroupCode.setText("");
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    if(GroupName.getText().toString() != null && GroupCode.getText().toString() != null){
                        GroupObjectClass newGroup = new GroupObjectClass();
                        newGroup.setGroupName(GroupName.getText().toString());
                        newGroup.setGroupCode(GroupCode.getText().toString());
                        db.collection("GROUPS")
                                .document(GroupName.getText().toString())
                                .set(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    db.collection("GROUPS")
                                            .document(GroupName.getText().toString())
                                            .collection("GROUPMEMBERS")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .set(current).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                db.collection("USERS")
                                                        .document(current.getUserID())
                                                        .collection("GROUPS")
                                                        .document(GroupName.getText().toString())
                                                        .set(newGroup)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "NEW GROUP CREATED", Toast.LENGTH_SHORT).show();
                                                                }
                                                                GroupName.setText("");
                                                                GroupCode.setText("");
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    }

                    else if(GroupName.getText().toString().equals(doc.toObject(GroupObjectClass.class).getGroupName())){
                        Toast.makeText(getApplicationContext(), "NAME ALREADY EXISTS...ENTER A NEW NAME", Toast.LENGTH_SHORT).show();
                        break;
                    }

            }
            }
        });




    }
}