    //Allows the user to create a new event using the DatePickerDialog, Google Places API, and TimePickerDialog

package com.kiaan.trider;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ObjectClasses.EventPlaces;
import ObjectClasses.User;

public class NewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText EventName;
    private EditText placeName;
    private EditText startLocale;
    private EditText eventStartTime;
    private EditText eventDate;
    private PlacesClient placesClient;
    private User user;
    private EventPlaces event;
    private String date;
    private Button saveDetailsButton;
    private FirebaseFirestore db;
    private Button UpdateDetails;
    private int hour;
    private String selectedEvent;
    private Button JoinEvent;
    private Button DeleteEvent;
    private String GroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        EventName = findViewById(R.id.EventName);
        placeName = findViewById(R.id.placeName);
        placeName.clearFocus();
        placeName.setFocusable(false);
        startLocale = findViewById(R.id.startLocal);
        startLocale.clearFocus();
        startLocale.setFocusable(false);
        eventDate = findViewById(R.id.eventDate);
        eventDate.clearFocus();
        eventDate.setFocusable(false);
        Places.initialize(this, "AIzaSyCqxRIstKsdm-Z_ujg-b61kc8Dv2UQFKHY");
        placesClient = Places.createClient(this);
        event = new EventPlaces();
        eventStartTime = findViewById(R.id.eventTime);
        eventStartTime.clearFocus();
        eventStartTime.setFocusable(false);
        saveDetailsButton = findViewById(R.id.saveDetailsButton);
        db = FirebaseFirestore.getInstance();
        DeleteEvent = findViewById(R.id.DeleteEventButton);
        UpdateDetails = findViewById(R.id.updateDetailsbutton);
        JoinEvent = findViewById(R.id.JoinEventButton);
        JoinEvent.setVisibility(View.INVISIBLE);
        UpdateDetails.setVisibility(View.INVISIBLE);
        DeleteEvent.setVisibility(View.INVISIBLE);
        user = new User();

        Intent intent = getIntent();
        selectedEvent = intent.getStringExtra("EventName");
        GroupName = intent.getStringExtra("GroupName");

        if (selectedEvent != null) {
            UpdateDetails.setVisibility(View.VISIBLE);
            DeleteEvent.setVisibility(View.VISIBLE);
            saveDetailsButton.setVisibility(View.INVISIBLE);



            db.collection("GROUPS")
                    .document(GroupName)
                    .collection("EVENTS").document(selectedEvent).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        event = task.getResult().toObject(EventPlaces.class);
                        JoinEvent.setVisibility(View.VISIBLE);
                        EventName.setText(task.getResult().toObject(EventPlaces.class).getEventName());
                        placeName.setText(task.getResult().toObject(EventPlaces.class).getPlaceName());
                        startLocale.setText(task.getResult().toObject(EventPlaces.class).getStartLocation());
                        eventDate.setText(task.getResult().toObject(EventPlaces.class).getPlaceDate());
                        eventStartTime.setText(task.getResult().toObject(EventPlaces.class).getStartTime());

                    } else {
                        Toast.makeText(getApplicationContext(), "UNABLE TO GET EVENT DETAILS", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        db.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user = task.getResult().toObject(User.class);
            }
        });

        DeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               db.collection("GROUPS")
                       .document(GroupName)
                       .collection("EVENTS").document(selectedEvent).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       finish();
                   }
               });
            }
        });

        JoinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("GROUPS")
                        .document(GroupName)
                        .collection("EVENTS")
                        .document(selectedEvent)
                        .collection("JOINEDUSERS")
                        .document(FirebaseAuth.getInstance().getUid())
                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"SUCCESSFULLY ADDED TO EVENT", Toast.LENGTH_SHORT);
                            Intent intent1 = new Intent(getApplicationContext(), MapFragment.class);
                            intent1.putExtra("PlaceName",event.getPlaceName());
                            intent1.putExtra("StartName",event.getStartLocation());
                            intent1.putExtra("EventGeoPointLatitude",event.getPlaceGeoPoint().getLatitude());
                            intent1.putExtra("EventGeoPointLongitude",event.getPlaceGeoPoint().getLongitude());
                            intent1.putExtra("StartEventGeoPointLatitude", event.getStartLocationGeoPoint().getLatitude());
                            intent1.putExtra("StartEventGeoPointLongitude", event.getStartLocationGeoPoint().getLongitude());
                            intent1.putExtra("EventName", event.getEventName());
                            intent1.putExtra("GroupName", GroupName);
                            startActivity(intent1);
                        }
                    }
                });
            }
        });
        UpdateDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                db.collection("GROUPS")
                        .document(GroupName)
                        .collection("EVENTS").document(selectedEvent).delete();
                db.collection("GROUPS")
                        .document(GroupName)
                        .collection("EVENTS")
                        .document(event.getEventName()).set(event)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "EVENT DETAILS UPDATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "UNABLE TO UPDATE EVENT DETAILS", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

        EventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                event.setEventName(editable.toString());
            }
        });

        placeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int AUTOCOMPLETE_REQUEST_CODE_END = 2;
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN,
                        Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_END);
                intent.putExtra("ActivityName", "EndLocal");
            }
        });

        startLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int AUTOCOMPLETE_REQUEST_CODE_START = 1;
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN,
                        Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_START);

            }
        });

        eventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        try {
            saveDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((EventName.getText() != null) ||
                            !placeName.getText().equals("END LOCATION NAME")
                            || !startLocale.getText().equals("START LOCATION NAME")
                            || !eventStartTime.getText().equals("EVENT TIME")
                            || !eventDate.getText().equals("EVENT DATE")) { saveDetilsInDB(); }

                    else { Toast.makeText(getApplicationContext(), "PLEASE INPUT ALL DETAILS", Toast.LENGTH_SHORT); }
                }
            });
        }catch (Exception e){ e.printStackTrace(); }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 2) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                event.setPlaceName(place.getName());
                placeName.setText(event.getPlaceName());
                event.setPlaceGeoPoint(new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude));
            } else if (requestCode == 1) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                event.setStartLocation(place.getName());
                startLocale.setText(event.getStartLocation());
                event.setStartLocationGeoPoint(new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Status", status.getStatusMessage());
            }

        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "FAILED TO ASSIGN LOCATION", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),true
        );
        timePickerDialog.show();
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if(i <12) {
            if(i1 < 10){
                event.setStartTime(String.valueOf(i) + ":" + String.valueOf(i1) + "0 AM");
                eventStartTime.setText(event.getStartTime());
            }
            else {
                event.setStartTime(String.valueOf(i) + ":" + String.valueOf(i1) + " AM");
                eventStartTime.setText(event.getStartTime());
            }
        }
        else if(i >=12){
            if(i1 < 10) {
                event.setStartTime(String.valueOf(i) + ":" + String.valueOf(i1) + "0 PM");
                eventStartTime.setText(event.getStartTime());
            }
            else{
                event.setStartTime(String.valueOf(i) + ":" + String.valueOf(i1) + " PM");
                eventStartTime.setText(event.getStartTime());
            }
        }
    }


    private void showDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        date =  dayOfMonth + " / " + (month+1) + " / " + year;
        event.setPlaceDate(date);
        eventDate.setText(event.getPlaceDate());
        Log.i("DATE",date);
    }

    private void saveDetilsInDB() {
        event.setHasEventStarted(false);
       db.collection("GROUPS")
               .document(GroupName)
               .collection("EVENTS")
               .document(event.getEventName()).set(event)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               Toast.makeText(getApplicationContext(),"NEW EVENT ADDED",Toast.LENGTH_SHORT).show();
           }
       });
    }
}




