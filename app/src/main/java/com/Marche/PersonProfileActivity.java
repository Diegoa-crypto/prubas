package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {
    private TextView userName, desde, email_user, userStatus;
    private CircleImageView userProfileImage;
    private ImageButton LikespostButton, CommentPostButton;
    private TextView fullNameTextView, userPhoneTextView, adressTextView, statusTextView, desdeTextView;
    private TextView DisplayNoOfLikes;
    private DatabaseReference LikesRef;
    private CircleImageView profileImageView;
    Boolean LikeChecker = false;
    FirebaseAuth fAuth;
    String userID;
    private FirebaseFirestore fStore;
    String receiverUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        LikespostButton=(ImageButton) findViewById(R.id.like_button);
        CommentPostButton=(ImageButton) findViewById(R.id.comment_button);
        DisplayNoOfLikes=(TextView) findViewById(R.id.diplay_no_of_likes);

        profileImageView = (CircleImageView) findViewById(R.id.my_profile_pic);
        fullNameTextView = (TextView) findViewById(R.id.my_profile_full_name);
        adressTextView = (TextView) findViewById(R.id.correo_user_profile);
        desdeTextView = (TextView) findViewById(R.id.date_user_profile);
        statusTextView = (TextView) findViewById(R.id.user_status);


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


/*
        fStore.collection("Usuarios")
                .whereEqualTo("usuarioID", ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                String name = document.getString("fName");
                                String address = document.getString("email");
                                fullNameTextView.setText(name);
                                adressTextView.setText(address);

                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                    }
                });

 */





/*
        LikespostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LikeChecker=true;

                LikesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(userID).hasChild(userID))

                    }
                    else{

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });*/
    }



}
