//The class is connected to the activity_group_details and sets the GUI to show the list of users part of the group and assign a function to the events button.
//Kiaan Upamanyu

package com.kiaan.trider;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ObjectClasses.EventPlaces;
import ObjectClasses.GroupObjectClass;
import ObjectClasses.User;

public class GroupDetails extends AppCompatActivity{

        private ArrayList<String> UserNameList;
        private ArrayAdapter arrayAdapter;
        private ArrayList<User> mUsers = new ArrayList<>();
        private FirebaseFirestore db;
        private ListenerRegistration mUserListEventListener;
        private ListView UserListView;
        private GroupObjectClass SelectedGroup;
        private TextView GroupName;
        private TextView groupCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        db = FirebaseFirestore.getInstance();
        UserListView = findViewById(R.id.MemberList);
        GroupName = findViewById(R.id.GroupName);
        UserNameList = new ArrayList<>();
        groupCodeText = findViewById(R.id.GroupCodeTextView);
        Intent intent = getIntent();
        String temp = intent.getStringExtra("GroupName");
        db.collection("GROUPS").document(temp).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){ SelectedGroup = task.getResult().toObject(GroupObjectClass.class);}
                else { Toast.makeText(getApplicationContext(), "UNABLE TO GET DETAILS", Toast.LENGTH_SHORT).show(); }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                setListView();
            }
        }, 2000);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment mFragment = new GroupsActivity();
             getSupportFragmentManager().beginTransaction().replace(R.id.group_fragment,mFragment).addToBackStack(null).commit();
            }
        };
    }

    //Queries the list of users who are part of the respective group from the database, stores them into an arrayList, and sets the listView of groupMember's names
    private void setListView()
    {
        GroupName.setText("GROUP NAME:"+SelectedGroup.getGroupName());
        groupCodeText.setText("GROUP CODE:"+SelectedGroup.getGroupCode());
        Button EventsButton = findViewById(R.id.EventListButton);
        EventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
                intent.putExtra("GroupName",SelectedGroup.getGroupName());
                startActivity(intent);
            }
        });

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        CollectionReference EventListRef = db.collection("GROUPS")
                .document(SelectedGroup.getGroupName())
                .collection("GROUPMEMBERS");

        mUserListEventListener = EventListRef.addSnapshotListener(MetadataChanges.EXCLUDE,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(e != null){
                        Log.e("ERROR","onEvent: ListenFailed",e);
                        return;
                    }
                    if(queryDocumentSnapshots != null){
                        UserNameList.clear();
                        UserNameList = new ArrayList<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            User user = doc.toObject(User.class);
                            UserNameList.add(user.getUserName());
                        }
                    }
                }
            });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, UserNameList);
                UserListView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }
        }, 2000);

    }

//The leave button allows a user to quit the particular group.
    public void LeaveFunc(View view) {
        db.collection("GROUPS").document(SelectedGroup.getGroupName()).collection("GROUPMEMBERS").document(FirebaseAuth.getInstance().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "LEFT GROUP SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    db.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("GROUPS").document(SelectedGroup.getGroupName()).delete();
                    Intent intent = new Intent(getApplicationContext(), NavigationBar.class);
                    startActivity(intent);
                }
            }
        });
    }
}