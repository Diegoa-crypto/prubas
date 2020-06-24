package com.Marche;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private CircleImageView profileImageView;
    private TextView fullNameTextView, userPhoneTextView, adressTextView, statusTextView, desdeTextView;
    FirebaseUser currentUser;
    FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private StorageReference storageProfilePrictureRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImageView = (CircleImageView) findViewById(R.id.my_profile_pic_my);
        fullNameTextView = (TextView) findViewById(R.id.my_profile_full_name_my);
        adressTextView = (TextView) findViewById(R.id.correo_user_profile_my);
        desdeTextView = (TextView) findViewById(R.id.date_user_profile_my);
        statusTextView = (TextView) findViewById(R.id.user_status_my);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();





        userInfoDisplay();


    }

    private void userInfoDisplay()
    {
        userID=fAuth.getCurrentUser().getUid();
        fStore.collection("Usuarios").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    String image = documentSnapshot.getString("image");
                    String name = documentSnapshot.getString("fName");
                    String phone = documentSnapshot.getString("fTelefono");
                    String address = documentSnapshot.getString("email");
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImageView);
                    fullNameTextView.setText(name);
                    adressTextView.setText(address);

                }
            }
        });
    }
}
