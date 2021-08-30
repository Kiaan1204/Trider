//Allows the user to either change or set his/her icon, to be shown on the map.

package com.kiaan.trider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileDetailsActivity extends AppCompatActivity {
    private ImageView black;
    private ImageView red;
    private ImageView green;
    private ImageView yellow;
    private ImageView blue;
    private ImageView violet;
    private String Selection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        red = findViewById(R.id.red);
        yellow = findViewById(R.id.yellow);
        black = findViewById(R.id.black);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);
        violet = findViewById(R.id.violet);
        Selection = String.valueOf(R.drawable.bikeblack);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   Selection = String.valueOf(R.drawable.bikered);
                   changeIntent();
            }
            });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selection = String.valueOf(R.drawable.bikegreen);
                changeIntent();
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selection = String.valueOf(R.drawable.bikeyellow);
                changeIntent();
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selection = String.valueOf(R.drawable.bikeblue);
                changeIntent();
            }
        });

        violet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selection = String.valueOf(R.drawable.bikeviolet);
                changeIntent();
            }
        });

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selection = String.valueOf(R.drawable.bikeblack);
                changeIntent();
            }
        });
    }


    public void changeIntent() {
        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).update("selectedBike",Selection).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "ICON SELECTED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "ICON SELECTED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent = new Intent(this, NavigationBar.class);
        startActivity(intent);
        finish();
    }
}
