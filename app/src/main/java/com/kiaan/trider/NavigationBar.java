//Sets the User's location into the database and sets the working of the sideNavigationBar

package com.kiaan.trider;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import ObjectClasses.User;
import ObjectClasses.UserLocation;
import service.LocationService;

import static ObjectClasses.Constants.ERROR_DIALOG_REQUEST;
import static ObjectClasses.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static ObjectClasses.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class NavigationBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;
    private static final String TAG = " STARTUP ACTIVITY";
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(mLocationPermissionGranted) {
                GetUserDetails();
                startLocationService();
            } else {
                getLocationPermission();
            }
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            return isMapsEnabled();
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    //Prompts the user to accept the location permissions.
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            GetUserDetails();
            startLocationService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(NavigationBar.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(NavigationBar.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);


        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            NoUser();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_closed);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, new HomeActivity()).commit();


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,
                        new HomeActivity()).commitNow();
                break;

            case R.id.nav_groups:
                getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,
                        new GroupsActivity()).commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,
                        new ProfileSettings()).commit();
                break;
            case R.id.logout:
                Toast.makeText(this, "LOGGED OUT", Toast.LENGTH_SHORT).show();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                finish();
                Intent intent = new Intent(this, StartUpActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    //Gets teh last location of a user
    private void getLastKnownLocation(){
        Log.d("getLastKnowLocation"," CALLED");
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    try {
                        Location location = task.getResult();
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d("onComplete lat", String.valueOf(geoPoint.getLatitude()));
                        Log.d("onComplete lat", String.valueOf(geoPoint.getLongitude()));
                        mUserLocation.setGeo_Point(geoPoint);
                        mUserLocation.setTime_stamp(null);
                        SaveUserLocation();
                    }catch (NullPointerException e){
                        Toast.makeText(getApplicationContext(), "Couldn't get location...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    //Saves the userlocation into the database.
    private void SaveUserLocation(){
        if(mUserLocation != null)
        {
            DocumentReference locationRef = FirebaseFirestore.getInstance()
                    .collection("USERLOCATIONS")
                    .document(FirebaseAuth.getInstance().getUid());
            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("saveUserLocation","inserted db" +"\n Latitude"+ mUserLocation.geo_Point.getLatitude() +"\n Longi"+mUserLocation.geo_Point.getLongitude());
                    }
                }
            });
        }
    }
    private void GetUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
            DocumentReference userRef = FirebaseFirestore.getInstance()
                    .collection("USERLOCATIONS")
                    .document(FirebaseAuth.getInstance().getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.i("succesful"," got details");
                    User user = task.getResult().toObject(User.class);
                    mUserLocation.setUser(user);
                    getLastKnownLocation();
                }
            });
        }else{

                    getLastKnownLocation();
            }

    }
//Starts the background location service.
    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                NavigationBar.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }
//Checks if the service is running. If not calls the startLocationService() function
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.kiaan.trider.service.LocationService".equals(service.service.getClassName())) {
                Log.d("Service run", "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d("run check", "isLocationServiceRunning: location service is not running.");
        return false;
    }


    private void NoUser(){
        finish();
        Intent intent = new Intent(this, StartUpActivity.class);
        startActivity(intent);
    }

}







