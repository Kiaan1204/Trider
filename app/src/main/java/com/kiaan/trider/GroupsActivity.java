//The class sets the listView to show the groups a user member of and also provides two buttons to guide the user to two different screens. One to create a group and one to join a group.
//Kiaan Upamanyu

package com.kiaan.trider;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

import ObjectClasses.GroupObjectClass;

public class GroupsActivity extends Fragment {
    private ListView groups;
    private ArrayList<String> GroupNameList;
    private Button JoinButton;
    private Button CreateButton;
    private ImageButton refresh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_groups, container, false);
        groups = view.findViewById(R.id.GroupListView);
        GroupNameList = new ArrayList<>();
        JoinButton = view.findViewById(R.id.JoinButton);
        CreateButton = view.findViewById(R.id.CreateButton);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("GROUPS").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        GroupNameList.add(doc.toObject(GroupObjectClass.class).getGroupName());
                    }
                }
            });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, GroupNameList);
                groups.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }
        }, 2000);

        groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(),GroupDetails.class);
                intent.putExtra("GroupName",groups.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getContext(), NavigationBar.class);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        JoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}