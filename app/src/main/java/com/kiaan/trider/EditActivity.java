package com.kiaan.trider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditActivity extends AppCompatActivity {
    private EditText Name;
    private EditText PhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Name = findViewById(R.id.NameEditText);
        PhoneNumber = findViewById(R.id.PhoneEditText);
    }

    public void SaveDetailsButton(View view) {

        if(!Name.getText().toString().equals(null) && !PhoneNumber.getText().toString().equals(null)){
            if(FirebaseFirestore.getInstance()
                    .collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .update("userName", Name.getText().toString()).isSuccessful() &&

                    FirebaseFirestore.getInstance()
                    .collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .update("userPhone", Name.getText().toString()).isSuccessful()){
                Toast.makeText(getApplicationContext(), "UPDATED DETAILS SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            }else{ Toast.makeText(getApplicationContext(), "UPDATED DETAILS UNSUCCESSFUL\nPLEASE TRY AGAIN", Toast.LENGTH_SHORT).show(); }
        }
    }
}