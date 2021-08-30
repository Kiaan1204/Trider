//Allows the user to create a new account and get saved into the FirebaseAuth database and the FirebaseFirestore database.

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import ObjectClasses.User;

public class SignUpActivity extends AppCompatActivity {

    EditText EmailEditText;
    EditText PasswordEditText;
    EditText ConfirmPasswordEditText;
    EditText Name;
    EditText Phone;
    User NewUser;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore dp = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EmailEditText = findViewById(R.id.EmailEditText);
        PasswordEditText = findViewById(R.id.PasswordEditText);
        ConfirmPasswordEditText = findViewById(R.id.ConfirmPasswordEditText);
        Name = findViewById(R.id.NameEditText);
        Phone = findViewById(R.id.PhoneEditText);
        NewUser = new User();

    }

    public void SignUp(final View view){

        if (EmailEditText.getText().length() == 0 || Name.getText().length() == 0 || Phone.getText().length() ==0){
            Toast.makeText(view.getContext(),"PLEASE ENTER ALL DETAILS DETAILS",Toast.LENGTH_SHORT).show();
        }
        else if (PasswordEditText.getText().length() <= 6){
            Toast.makeText(view.getContext(),"PASSWORD MUST BE AT LEAST 6 CHARACTERS",Toast.LENGTH_SHORT).show();
        }
        else if(PasswordEditText.getText().toString().equals(ConfirmPasswordEditText.getText().toString()) && EmailEditText.getText() != null && Name.getText() != null && Phone.getText() !=null)
        {
            mAuth.createUserWithEmailAndPassword(EmailEditText.getText().toString(), PasswordEditText.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        NewUser.setUserEmail(EmailEditText.getText().toString());
                        NewUser.setUserName(Name.getText().toString());
                        NewUser.setUserPhone(Phone.getText().toString());
                        NewUser.setUserID(FirebaseAuth.getInstance().getUid());
                        FirebaseFirestore.getInstance()
                                .collection("USERS")
                                .document(FirebaseAuth.getInstance().getUid())
                                .set(NewUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.i("SIGN UP:", "success");
                                    changeToUserDetails();
                                    finish();
                                }
                            }
                        });
                    }
                    else {
                        Log.i("SIGN UP:", "failure", task.getException());
                        Toast.makeText(view.getContext(), Objects.requireNonNull(task.getException()).toString().substring(task.getException().toString().indexOf(" ")),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else {
            Toast.makeText(view.getContext(),"PASSWORDS DO NOT MATCH",Toast.LENGTH_SHORT).show();
        }
    }
    private void changeToUserDetails() {
        Intent intent = new  Intent(this,ProfileDetailsActivity.class);
        startActivity(intent);
    }

}
