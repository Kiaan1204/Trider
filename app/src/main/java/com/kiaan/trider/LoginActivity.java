//The class sets the activity_login GUI and allows the user to login using an email and password, set by the user.

package com.kiaan.trider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity {
    EditText EmailEditText = null;
    EditText PasswordEditText = null;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EmailEditText = findViewById(R.id.EmailEditText);
        PasswordEditText = findViewById(R.id.PasswordEditText);


    }

//Changes the current activity to the NavigationBar and HomePage.
    private void LogIn() {
        Intent intent = new  Intent(this,NavigationBar.class);
        startActivity(intent);
        finish();
    }

    //Uses firebaseAuth to log the user into his/her account.
    public void loggedIn(final View view){
        mAuth.signInWithEmailAndPassword(EmailEditText.getText().toString(), PasswordEditText.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i("SIGN IN:", "signInWithEmail:success");
                    LogIn();
                } else {

                    Log.i("SIGN IN:", "signInWithEmail:failure", task.getException());
                    Toast.makeText(view.getContext(), Objects.requireNonNull(task.getException()).toString().substring(task.getException().toString().indexOf(" ")), LENGTH_SHORT).show();

                }
            }
        });


    }

}
