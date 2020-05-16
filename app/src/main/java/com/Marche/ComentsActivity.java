package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.Marche.Perfil.Coomments;
import com.Marche.ViewHolder.CommentsViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ComentsActivity extends AppCompatActivity
{
    private RecyclerView CommentsList;
    RecyclerView.LayoutManager layoutManager;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private String user_id;
    private FirebaseAuth fAuth;
    private String userID;
    private FirebaseFirestore fStore;
    private FirestoreRecyclerAdapter<Coomments, CommentsViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);





        CommentsList = findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        CommentsList.setLayoutManager(layoutManager);

        CommentInputText=(EditText)findViewById(R.id.comment_input);
        PostCommentButton=(ImageButton) findViewById(R.id.post_comment_btn);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        user_id=getIntent().getExtras().get("user_id").toString();
        userID = fAuth.getCurrentUser().getUid();





        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DocumentReference docoRef = fStore.collection("Usuarios").document(userID);
                docoRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("fName");
                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }

                    }
                });

            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Query query = fStore.collection("Usurios").document(user_id).collection("Comments")
                .orderBy("date");
        FirestoreRecyclerOptions<Coomments> options = new FirestoreRecyclerOptions.Builder<Coomments>()
                .setQuery(query,Coomments.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Coomments, CommentsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Coomments model)
                    {
                        holder.myUserName.setText(model.getUsername()+"  ");
                        holder.myDate.setText("  Fecha "+model.getDate());
                        holder.myTime.setText("  Hora "+model.getTime());
                        holder.myComment.setText(model.getComment());

                    }

                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent,false);
                        CommentsViewHolder holder = new CommentsViewHolder(view );
                        return holder;

                    }
                };

        CommentsList.setAdapter(adapter);
        adapter.startListening();


        //CollectionReference userref=fStore.collection("Usurios").document(user_id).collection("Comments");




    }
    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void ValidateComment(String userName)
    {
        String commentText = CommentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Escribe un comentario", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            final String RandomKey = userID+currentDate+currentTime;

            DocumentReference comentRef=fStore
                    .collection("Usuarios").document(user_id)
                    .collection("Comments").document(RandomKey);
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("uid",userID);
            userMap.put("comment",commentText);
            userMap.put("date",saveCurrentDate);
            userMap.put("time",saveCurrentTime);
            userMap.put("username",userName);
            comentRef.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(ComentsActivity.this,"Comentario exitoso. Gracias...", Toast.LENGTH_SHORT).show();

                }
            });


        }
    }
}
