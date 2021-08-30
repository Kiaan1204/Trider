//The class sets the MapFragment and gets the Routes, JoinedUsers icons, and a refresh bottom.
//Kiaan Upamanyu

package com.kiaan.trider;

    import androidx.activity.OnBackPressedCallback;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;

    import android.Manifest;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.icu.lang.UScript;
    import android.location.Location;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.Toast;

    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
    import com.google.android.gms.maps.MapView;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.LatLngBounds;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.Polyline;
    import com.google.android.gms.maps.model.PolylineOptions;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.CollectionReference;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.EventListener;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.FirebaseFirestoreException;
    import com.google.firebase.firestore.FirebaseFirestoreSettings;
    import com.google.firebase.firestore.GeoPoint;
    import com.google.firebase.firestore.ListenerRegistration;
    import com.google.firebase.firestore.MetadataChanges;
    import com.google.firebase.firestore.QueryDocumentSnapshot;
    import com.google.firebase.firestore.QuerySnapshot;
    import com.google.maps.DirectionsApiRequest;
    import com.google.maps.GeoApiContext;
    import com.google.maps.PendingResult;
    import com.google.maps.android.clustering.ClusterManager;
    import com.google.maps.internal.PolylineEncoding;
    import com.google.maps.model.DirectionsResult;
    import com.google.maps.model.DirectionsRoute;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;

    import ObjectClasses.ClusterMarker;
    import ObjectClasses.PolyLineData;
    import ObjectClasses.User;
    import ObjectClasses.UserLocation;
    import util.MyClusterManagerRenderer;

    import static ObjectClasses.Constants.MAPVIEW_BUNDLE_KEY;


    public class MapFragment extends AppCompatActivity implements
            OnMapReadyCallback, OnInfoWindowClickListener,
            GoogleMap.OnPolylineClickListener {

        private MapView mMapView;
        private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
        private ArrayList<User> mUsers = new ArrayList<>();
        private ListenerRegistration mUserListEventListener;
        private GoogleMap mGoogleMap;
        private LatLngBounds mMapBoundry;
        private UserLocation mUserPosition;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private ClusterManager mClusterManager;
        private MyClusterManagerRenderer mClusterManagerRenderer;
        private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
        private Handler mHandler = new Handler();
        private Runnable mRunnable;
        private static final int LOCATION_UPDATE_INTERVAL = 3000;
        private GeoApiContext mGeoApiContext = null;
        private ArrayList<PolyLineData> mPolylineData = new ArrayList<>();
        private Marker selectedMarker = null;
        private ArrayList<Marker> tripMarkers = new ArrayList<>();
        private String selectedEvent;
        private LatLng startLatLng;
        private LatLng endLatLng;
        private String startName;
        private String endName;
        private String GroupName;
        private Button ExitBtn;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map_fragment);
            mMapView = findViewById(R.id.user_list_map);
            selectedEvent = getIntent().getStringExtra("EventName");
            double endLat = getIntent().getDoubleExtra("EventGeoPointLatitude", 0);
            double endLong = getIntent().getDoubleExtra("EventGeoPointLongitude", 0);
            double startLat = getIntent().getDoubleExtra("StartEventGeoPointLatitude", 0);
            double startLong = getIntent().getDoubleExtra("StartEventGeoPointLongitude", 0);
            startName = getIntent().getStringExtra("StartName");
            endName = getIntent().getStringExtra("PlaceName");
            GroupName = getIntent().getStringExtra("GroupName");
            ExitBtn = findViewById(R.id.btn_exit);
            ExitBtn.setVisibility(0);


            startLatLng = new LatLng(startLat, startLong);
            endLatLng = new LatLng(endLat,endLong);
            findViewById(R.id.btn_reset_map).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMapMarkers();
                }
            });

            initGoogleMap(savedInstanceState);
            getUsers();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    setUserPosition();
                    ExitBtn.setVisibility(1);
                }
            }, 5000);


//            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//                @Override
//                public void handleOnBackPressed() {
//                    db.collection("GROUPS")
//                            .document(GroupName)
//                            .collection("EVENTS")
//                            .document(selectedEvent)
//                            .collection("JOINEDUSERS")
//                            .document(FirebaseAuth.getInstance().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(),"SUCCESSFULLY EXITED EVENT", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(), NavigationBar.class);
//                                startActivity(intent);
//                            }
//                        }
//                    });
//                }
//            };

            }

//Resets the map by clearing all the data and refreshing the map.
            private void resetMap(){
            if(mGoogleMap != null) {
                mGoogleMap.clear();
                if(mClusterManager != null){
                    mClusterManager.clearItems();
                }

                if (mClusterMarkers.size() > 0) {
                    mClusterMarkers.clear();
                    mClusterMarkers = new ArrayList<>();
                }

                if(mPolylineData.size() > 0){
                    mPolylineData.clear();
                    mPolylineData = new ArrayList<>();
                }
            }
        }

//Zooms the map according to the route to be travelled.
        public void zoomRoute(List<LatLng> lstLatLngRoute) {

            if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : lstLatLngRoute)
                boundsBuilder.include(latLngPoint);

            int routePadding = 120;
            LatLngBounds latLngBounds = boundsBuilder.build();

            mGoogleMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                    600,
                    null
            );
        }

 //Removes the marker of a particular route to a user.
        private void removeTripMarkers(){
            for(Marker marker:tripMarkers){
                marker.remove();
            }
        }
        private void resetSelectedMarker(){
            if(selectedMarker != null){
                selectedMarker.setVisible(true);
                selectedMarker=null;
                removeTripMarkers();
            }
        }

//Adds the blue line to represent the route from start location to end location
        private void addPolylinesToMap(final DirectionsResult result){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d("Polylines", "run: result routes: " + result.routes.length);
                    if(mPolylineData.size() > 0){
                        for(PolyLineData polyLineData : mPolylineData){
                            polyLineData.getPolyline().remove();
                        }
                        mPolylineData.clear();
                        mPolylineData = new ArrayList<>();
                    }
                    for(DirectionsRoute route: result.routes){
                        Log.d("Polylines", "run: leg: " + route.legs[0].toString());
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                        List<LatLng> newDecodedPath = new ArrayList<>();
                        double duration = 99999999;
                        // This loops through all the LatLng coordinates of ONE polyline.
                        for(com.google.maps.model.LatLng latLng: decodedPath){

    //                        Log.d(TAG, "run: latlng: " + latLng.toString());

                            newDecodedPath.add(new LatLng(
                                    latLng.lat,
                                    latLng.lng
                            ));
                        }
                        Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.quantum_grey));
                        polyline.setClickable(true);
                        mPolylineData.add(new PolyLineData(polyline, route.legs[0]));

                        double tempDuration = route.legs[0].duration.inSeconds;
                        if(tempDuration < duration){
                            duration = tempDuration;
                            onPolylineClick(polyline);
                            zoomRoute(polyline.getPoints());
                        }
                        selectedMarker.setVisible(false);
                    }
                }
            });
        }

 //Starts a runnable to get all the userLocations
        private void startUserLocationsRunnable(){
            Log.d("ClusterMarkerRunnable", "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
            mHandler.postDelayed(mRunnable = new Runnable() {
                @Override
                public void run() {
                    retrieveUserLocations();
                    mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
                }
            }, LOCATION_UPDATE_INTERVAL);
        }

        private void stopLocationUpdates(){
            mHandler.removeCallbacks(mRunnable);
        }

//Gets the locations of the users who have joined the particular.
        private void retrieveUserLocations(){
            Log.d("ClusterMarkerRunnable", "retrieveUserLocations: retrieving location of all users.");

            try{
                for(final ClusterMarker clusterMarker: mClusterMarkers){

                    DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                            .collection("USERLOCATIONS")
                            .document(clusterMarker.getUser().getUserID());

                    userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){

                                final UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);

                                // update the location
                                for (int i = 0; i < mClusterMarkers.size(); i++) {
                                    try {
                                        if (mClusterMarkers.get(i).getUser().getUserID().equals(updatedUserLocation.getUser().getUserID())) {

                                            LatLng updatedLatLng = new LatLng(
                                                    updatedUserLocation.getGeo_Point().getLatitude(),
                                                    updatedUserLocation.getGeo_Point().getLongitude()
                                            );
                                            mClusterMarkers.get(i).setPosition(updatedLatLng);
                                            mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                                        }


                                    } catch (NullPointerException e) {
                                        Log.e("ClusterMarkerRunnable", "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    });
                }
            }catch (IllegalStateException e){
                Log.e("ClusterMarkerRunnable", "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.getMessage() );
            }

        }

//Sets the camera view based on the current user's location
        private void setCameraView(){
            //MAP BOUNDARIES OVERALL=0.2*0.2 = 0.04
            double bottomBoundry = mUserPosition.geo_Point.getLatitude()-.1;
            double leftBoundry = mUserPosition.geo_Point.getLongitude()-.1;
            double topBoundry = mUserPosition.geo_Point.getLatitude()+.1;
            double rightBoundry = mUserPosition.geo_Point.getLongitude()+.1;
            mMapBoundry = new LatLngBounds(new LatLng(bottomBoundry,leftBoundry),
                    new LatLng(topBoundry,rightBoundry)
            );
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundry, 0));
        }

//Adds the mapMarkers/ icons of the Users.
        private void addMapMarkers(){
            if(mGoogleMap != null){
                    resetMap();
                    mGoogleMap.addMarker(new MarkerOptions().position(startLatLng).title(startName));
                    mGoogleMap.addMarker(new MarkerOptions().position(endLatLng).title(endName));
                if(mClusterManager == null){
                    mClusterManager = new ClusterManager<ClusterMarker>(this.getApplicationContext(), mGoogleMap);
                }
                if(mClusterManagerRenderer == null){
                    mClusterManagerRenderer = new MyClusterManagerRenderer(
                            this,
                            mGoogleMap,
                            mClusterManager
                    );
                    mClusterManager.setRenderer(mClusterManagerRenderer);
                }
                for(UserLocation userLocation: mUserLocations){

                    Log.d("MarkerLog", "addMapMarkers: location: " + userLocation.getGeo_Point().toString());
                    try{
                        String snippet = "";
                        if(userLocation.getUser().getUserID().equals(FirebaseAuth.getInstance().getUid())){
                            snippet = "This is you";
                        }
                        else{
                            snippet = "Determine route to " + userLocation.getUser().getUserName() + "?";
                        }
                        int avatar = R.drawable.bikeblack; // set the default avatar
                        try{
                            avatar = Integer.parseInt(userLocation.getUser().getSelectedBike());
                        }catch (NumberFormatException e){
                            Log.d("MarkerLog", "addMapMarkers: no avatar for " + userLocation.getUser().getUserName() + ", setting default.");
                        }
                        ClusterMarker newClusterMarker = new ClusterMarker(
                                new LatLng(userLocation.getGeo_Point().getLatitude(), userLocation.getGeo_Point().getLongitude()),
                                userLocation.getUser().getUserName(),
                                snippet,
                                avatar,
                                userLocation.getUser()
                        );
                        mClusterManager.addItem(newClusterMarker);
                        mClusterMarkers.add(newClusterMarker);

                    }catch (NullPointerException e){
                        Log.e("MarkerLog", "addMapMarkers: NullPointerException: " + e.getMessage() );
                    }

                }
                mClusterManager.cluster();
                setCameraView();
            }
        }

//Sets the currently Logged in user's loocation
        private void setUserPosition(){
            for(UserLocation userLocation : mUserLocations){
                if(userLocation.getUser().getUserID().equals(FirebaseAuth.getInstance().getUid())){
                    mUserPosition = userLocation;
                }
            }
        }

//Gets the Users part of the event to get their locations
        private void getUsers() {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
            CollectionReference locationRef = db.collection("GROUPS")
                    .document(GroupName)
                    .collection("EVENTS")
                    .document(selectedEvent)
                    .collection("JOINEDUSERS");
            mUserListEventListener = locationRef.addSnapshotListener(MetadataChanges.EXCLUDE,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(e != null){
                        Log.e("ERROR","onEvent: ListenFailed",e);
                        return;
                    }
                    if(queryDocumentSnapshots != null){
                        mUsers.clear();
                        mUsers = new ArrayList<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            User user = doc.toObject(User.class);
                            mUsers.add(user);
                            getUserLocation(user);
                        }

                    }
                }
            });
        }

//Gets the User's locations from the database.
        private void getUserLocation(User user) {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
            DocumentReference locationRef = db
                    .collection("USERLOCATIONS")
                    .document(user.getUserID());
            locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().toObject(UserLocation.class) != null){
                            mUserLocations.add(task.getResult().toObject(UserLocation.class));
                        }
                    }
                }
            });
        }

//Calculates the direction from the start location to any end location
        private void calculateDirections(Marker marker){
            Log.d("DirectionCalculate", "calculateDirections: calculating directions.");

            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    marker.getPosition().latitude,
                    marker.getPosition().longitude
            );
            DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

            directions.alternatives(true);
            directions.origin(
                    new com.google.maps.model.LatLng(
                            mUserPosition.getGeo_Point().getLatitude(),
                            mUserPosition.getGeo_Point().getLongitude()
                    )
            );
            Log.d("DirectionCalculate", "calculateDirections: destination: " + destination.toString());
            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    Log.d("DirectionCalculate", "calculateDirections: routes: " + result.routes[0].toString());
                    Log.d("DirectionCalculate", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                    Log.d("DirectionCalculate", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                    Log.d("DirectionCalculate", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                    addPolylinesToMap(result);

                }
                @Override
                public void onFailure(Throwable e) {
                    Log.e("DirectionCalculate", "calculateDirections: Failed to get directions: " + e.getMessage() );
                }
            });
        }


        private void initGoogleMap(Bundle savedInstanceState){
            Bundle mapViewBundle = null;
            if(savedInstanceState != null){
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }
            mMapView.onCreate(mapViewBundle);
            mMapView.getMapAsync(this);

            if(mGeoApiContext == null)
            {
                mGeoApiContext =new GeoApiContext.
                        Builder().
                        apiKey("AIzaSyCqxRIstKsdm-Z_ujg-b61kc8Dv2UQFKHY")
                        .build();
            }

        }

        @Override
        public void onResume() {
            //Log.d(TAG, "LifeCycle Event: onResume: called.");
            mMapView.onResume();
            startUserLocationsRunnable(); // update user locations every 'LOCATION_UPDATE_INTERVAL'
            super.onResume();
        }

        @Override
        public void onStart() {
            //Log.d(TAG, "LifeCycle Event: onStart: called.");
            mMapView.onStart();
            super.onStart();
        }

        @Override
        public void onStop() {
            //Log.d(TAG, "LifeCycle Event: onStop: called.");
            mMapView.onStop();
            super.onStop();
        }

        @Override
        public void onMapReady(final GoogleMap map){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            mGoogleMap = map;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    addMapMarkers();
                }
            }, 5000);
            mGoogleMap.setOnInfoWindowClickListener(this);
            mGoogleMap.setOnPolylineClickListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            mMapView.onPause();
        }

        @Override
        public void onDestroy() {
            //Log.d(TAG, "LifeCycle Event: onDestroy: called.");
            super.onDestroy();
            mMapView.onDestroy();
            stopLocationUpdates();
        }

        @Override
        public void onLowMemory() {
            //Log.d(TAG, "LifeCycle Event: onLowMemory: called.");
            mMapView.onLowMemory();
            super.onLowMemory();
        }

        @Override
        public void onInfoWindowClick(final Marker marker) {
            if(marker.getTitle().contains("Trip: #")){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Open Google Maps?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String latitude = String.valueOf(marker.getPosition().latitude);
                                String longitude = String.valueOf(marker.getPosition().longitude);
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");

                                try{
                                    if (mapIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    }
                                }catch (NullPointerException e){
                                    Log.e("onInfoWindowClick", "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                    Toast.makeText(getApplicationContext(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

            else if(marker.getTitle().equals(startName)){
                Toast.makeText(this,"Start Location for Event", Toast.LENGTH_SHORT);
            }
            else if(marker.getTitle().equals(endName)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Navigate to: "+marker.getTitle())
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                resetSelectedMarker();
                                selectedMarker = marker;
                                calculateDirections(marker);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }


            else{
                if(marker.getSnippet().equals("This is you")){
                    marker.hideInfoWindow();
                }
                else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(marker.getSnippet())
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    resetSelectedMarker();
                                    selectedMarker = marker;
                                    calculateDirections(marker);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        }

            @Override
            public void onPolylineClick(Polyline polyline) {
                int index = 0;
                for(PolyLineData polylineData: mPolylineData){
                    index++;
                    Log.d("PolyLine Called", "onPolylineClick: toString: " + polylineData.toString());
                    if(polyline.getId().equals(polylineData.getPolyline().getId())){
                        polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.quantum_googblue));
                        polylineData.getPolyline().setZIndex(1);

                        LatLng endLocation = new LatLng(
                                polylineData.getLeg().endLocation.lat,
                                polylineData.getLeg().endLocation.lng
                        );
                        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(endLocation).
                                title("Trip: #"+index)
                                .snippet("Duration: "+polylineData.getLeg().duration)
                        );
                        marker.showInfoWindow();
                        tripMarkers.add(marker);
                    }
                    else{
                        polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.quantum_grey));
                        polylineData.getPolyline().setZIndex(0);
                    }
                }
            }
         public void ExitPressed(View view){
             db.collection("GROUPS")
                     .document(GroupName)
                     .collection("EVENTS")
                     .document(selectedEvent)
                     .collection("JOINEDUSERS")
                     .document(FirebaseAuth.getInstance().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()) {
                         Toast.makeText(getApplicationContext(),"SUCCESSFULLY EXITED EVENT", Toast.LENGTH_SHORT).show();
                         Intent intent = new Intent(getApplicationContext(), NavigationBar.class);
                         startActivity(intent);
                     }
                 }
             });
         }


        }

