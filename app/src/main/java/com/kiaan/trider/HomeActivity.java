//The class sets the activity_home's interface and shows the user's selected Icon, a refresh button-incase the location is unable to be gotten- and a TextView to set the user's name.
//Kiaan Upamanyu

package com.kiaan.trider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import ObjectClasses.User;
import ObjectClasses.UserLocation;

public class HomeActivity extends Fragment {
    private TextView Name;
    private ImageView DP;
    private ImageButton refresh;
    private TextView currentLocale;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_home, container, false);
        Name = view.findViewById(R.id.NameTextView);
        DP = view.findViewById(R.id.ProfileimageView);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            NoUser();
        }

        refresh = view.findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent intent = new Intent(getContext(), NavigationBar.class);
                startActivity(intent);
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("USERS").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addSnapshotListener(MetadataChanges.EXCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                Name.setText(Objects.requireNonNull(documentSnapshot.toObject(User.class)).getUserName());;
            }
        });


    try {

        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int ImageId = Integer.parseInt(documentSnapshot.toObject(User.class).getSelectedBike());
                Drawable drawable = getResources().getDrawable(ImageId);
                DP.setImageDrawable(drawable);
            }
        });
    }catch(Exception e)
    {
        DP.setImageDrawable(getResources().getDrawable(R.drawable.bikeblack));
    }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("ARE YOU SURE YOU WANT TO LOGOUT?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        requireActivity().finish();
                        Intent intent = new Intent(getContext(),StartUpActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }
    private void NoUser(){
        Intent intent = new Intent(getContext(), StartUpActivity.class);
        startActivity(intent);
    }

}

