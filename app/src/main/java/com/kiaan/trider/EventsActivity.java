//This class is connected to the activity_events and helps in setting the listView of the events of a particular group.
//Kiaan Upamanyu

package com.kiaan.trider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ObjectClasses.EventPlaces;
import ObjectClasses.User;

import static android.app.Activity.RESULT_CANCELED;
import static java.security.AccessController.getContext;

public class EventsActivity extends AppCompatActivity {
    private ListView Events;
    private ArrayList<String> EventNameList;
    private Button AddEvent;
    private String date;
    private EventPlaces event;
    private ArrayAdapter arrayAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration mUserListEventListener;
    private ArrayList<EventPlaces> mEvents = new ArrayList<>();
    private String GroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        AddEvent = findViewById(R.id.AddEventButton);
        Events = findViewById(R.id.EventListView);
        EventNameList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        GroupName = intent.getStringExtra("GroupName");

        setListView();

        AddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
                intent.putExtra("GroupName", GroupName);
                startActivity(intent);
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), NavigationBar.class);
                startActivity(intent);
            }
        };
    }

    //Sets the listView of the list of events of the group by querying the events of the respective group from the database and stores them in an array list to set the ListView.
    private void setListView()
    {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        CollectionReference EventListRef = db.collection("GROUPS")
                .document(GroupName)
                .collection("EVENTS");
        mUserListEventListener = EventListRef.addSnapshotListener(MetadataChanges.EXCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("ERROR","onEvent: ListenFailed",e);
                    return;
                }
                if(queryDocumentSnapshots != null){
                    mEvents.clear();
                    mEvents = new ArrayList<>();
                    EventNameList.clear();
                    EventNameList = new ArrayList<>();

                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        EventPlaces eventPlaces = doc.toObject(EventPlaces.class);
                        mEvents.add(eventPlaces);
                        EventNameList.add(eventPlaces.getEventName());
                    }
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, EventNameList);
                Events.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                Events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.i("view",Events.getItemAtPosition(i).toString());
                        Intent intent = new Intent(getApplicationContext(),NewEventActivity.class);
                        intent.putExtra("EventName",Events.getItemAtPosition(i).toString());
                        intent.putExtra("GroupName", GroupName);
                        startActivity(intent);

                    }
                });
            }
        }, 2000);

    }

}





