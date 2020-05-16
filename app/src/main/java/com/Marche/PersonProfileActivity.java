package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;

import com.Marche.Perfil.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private Boolean LikeChecker = false;
    private FirebaseAuth fAuth;
    private String user_id;
    private FirebaseFirestore fStore;
    private String userID;
    private int countLikes;




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

        //PostKey = getIntent().getExtras().get("PostKey").toString();


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        user_id=getIntent().getExtras().get("user_id").toString();
        userID = fAuth.getCurrentUser().getUid();

        LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        fStore.collection("Usuarios").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Picasso.get().load(documentSnapshot.getString("image")).into(profileImageView);
                fullNameTextView.setText(documentSnapshot.getString("fName"));
                desdeTextView.setText(documentSnapshot.getString("date"));
                adressTextView.setText(documentSnapshot.getString("email"));
                setLikeButtonStatus(user_id);

            }
        });


        LikespostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LikeChecker=true;
                LikesRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(LikeChecker.equals(true)){

                            if(dataSnapshot.child(user_id).hasChild(userID))
                            {
                                LikesRef.child(user_id).child(userID).removeValue();
                                LikeChecker=false;

                            }
                            else{
                                LikesRef.child(user_id).child(userID).setValue(true);
                                LikeChecker=false;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
        CommentPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(PersonProfileActivity.this, ComentsActivity.class);
                commentsIntent.putExtra("user_id",user_id);
                startActivity(commentsIntent);
            }
        });
    }
    public void setLikeButtonStatus(final String user_id)
    {
        LikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(user_id).hasChild(userID)){
                    countLikes = (int) dataSnapshot.child(user_id).getChildrenCount();
                    LikespostButton.setImageResource(R.drawable.like);
                    DisplayNoOfLikes.setText(Integer.toString(countLikes)+(" Me gusta"));
                }
                else{
                    countLikes = (int) dataSnapshot.child(user_id).getChildrenCount();
                    LikespostButton.setImageResource(R.drawable.dislike);
                    DisplayNoOfLikes.setText(Integer.toString(countLikes)+(" Me gusta"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
