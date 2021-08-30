//Provides the user with the option to either login or signup. If a user is already detected, it will directly guide the user to the home page.

package com.kiaan.trider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

import static ObjectClasses.Constants.ERROR_DIALOG_REQUEST;
import static ObjectClasses.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static ObjectClasses.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class StartUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LogIn();
        }
    }


    private void LogIn() {
        Intent intent = new Intent(this, NavigationBar.class);
        startActivity(intent);
    }

    public void SignUpPage(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void LoginPage(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}

