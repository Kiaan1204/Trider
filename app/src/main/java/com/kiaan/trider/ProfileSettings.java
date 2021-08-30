//Allows the user to deactivate the account or select a new icon.

package com.kiaan.trider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import ObjectClasses.User;

import static android.app.Activity.RESULT_OK;

public class ProfileSettings extends Fragment {
    private TextView UName;
    private TextView UEmail;
    private TextView UPhone;
    private ImageView UDP;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile_settings, container, false);
        Button update = view.findViewById(R.id.dpChangeButton);
//        Button editClick = view.findViewById(R.id.EditButton);
        Button deactivateClick = view.findViewById(R.id.DeactivateButton);
        UName = view.findViewById(R.id.UpdateName);
        UEmail = view.findViewById(R.id.UpdateEmail);
        UPhone = view.findViewById(R.id.UpdatePhoneNumber);
        UDP = view.findViewById(R.id.UpdateDP);


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getContext(), NavigationBar.class);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ProfileDetailsActivity.class);
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
                UName.setText(Objects.requireNonNull(documentSnapshot.toObject(User.class)).getUserName());
                UEmail.setText(Objects.requireNonNull(documentSnapshot.toObject(User.class)).getUserEmail());
                UPhone.setText(Objects.requireNonNull(documentSnapshot.toObject(User.class)).getUserPhone());

            }
        });

        deactivateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("ARE YOU SURE YOU WANT TO DEACTIVATE YOUR ACCOUNT?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                           FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                               FirebaseFirestore.getInstance().collection("USERLOCATIONS").document(FirebaseAuth.getInstance().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                     FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {
                                             Toast.makeText(view.getContext(),"ACCOUNT DELETED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                                         }
                                     }).addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(view.getContext(),"ACCOUNT DELETION NOT SUCCESSFUL TRY AGAIN",Toast.LENGTH_SHORT).show();
                                         }
                                     });
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(view.getContext(),"ACCOUNT DELETION NOT SUCCESSFUL TRY AGAIN",Toast.LENGTH_SHORT).show();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(view.getContext(),"ACCOUNT DELETION NOT SUCCESSFUL TRY AGAIN",Toast.LENGTH_SHORT).show();
                                   }
                               });
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(view.getContext(),"ACCOUNT DELETION NOT SUCCESSFUL TRY AGAIN",Toast.LENGTH_SHORT).show();
                               }
                           });


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
        });

        return view;
    }

}
