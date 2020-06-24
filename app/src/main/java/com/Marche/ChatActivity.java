package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Marche.Perfil.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChattoolBar;

    private ImageButton SendMessageButton;
    private EditText userMessageInput;

    private String messageReceicverID, messageReceiverName, messageSenderID;
    private DatabaseReference RootRef;
    private String saveCurrentDate, saveCurrentTime;

    private FirebaseAuth mAuth;



    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messageAdapter;
    private TextView username;
    FirebaseUser fuser;
    private CircleImageView receiverProfileImage;
    private SharedPreferences sharedPreferences;


    Intent intent;

    private FirebaseFirestore fStore;

    List<Messages> mMensaje;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.messages_list_users);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        username=(TextView) findViewById(R.id.username);

        SendMessageButton=(ImageButton) findViewById(R.id.send_menssage_button);
        // SendImagefileButton=(ImageButton) findViewById(R.id.send_image_file_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);
        receiverProfileImage=(CircleImageView) findViewById(R.id.profile_image);

        RootRef= FirebaseDatabase.getInstance().getReference();

        mAuth= FirebaseAuth.getInstance();

        fuser= FirebaseAuth.getInstance().getCurrentUser();

        messageSenderID=mAuth.getCurrentUser().getUid();

        fStore = FirebaseFirestore.getInstance();

        intent = getIntent();

        messageReceicverID=intent.getStringExtra("userid");

        //messageReceiverName=getIntent().getExtras().get("userName").toString();

        fStore.collection("Usuarios").document(messageReceicverID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null && documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("fName");
                    username.setText(name);
                }

            }
        });

        //sharedPreferences=getSharedPreferences("nombre",Context.MODE_PRIVATE);
        //final String pname = sharedPreferences.getString("pname","nada");

        //Picasso.get().load(pimage).placeholder(R.drawable.nini).into(receiverProfileImage);


        SendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String msg=userMessageInput.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(fuser.getUid(),messageReceicverID,msg);
                }else {
                    Toast.makeText(ChatActivity.this, "Necesitas escribir un mensaje", Toast.LENGTH_SHORT).show();
                }
                userMessageInput.setText("");

            }
        });
        readMessage(fuser.getUid(),messageReceicverID);



    }
    private void sendMessage(String from, String para, String message)
    {

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("type", "text");
        hashMap.put("from", from);
        hashMap.put("para", para);


        reference.child("Messages").push().setValue(hashMap);

/*
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(messageReceicverID);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(messageReceicverID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

 */



    }
    private void readMessage(final String myid, final String userid){
        mMensaje=new ArrayList<>();

        RootRef=FirebaseDatabase.getInstance().getReference("Messages");
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMensaje.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages messages=snapshot.getValue(Messages.class);
                    if(messages.getPara().equals(myid) && messages.getFrom().equals(userid)||
                        messages.getPara().equals(userid) && messages.getFrom().equals(myid)){
                        mMensaje.add(messages);
                    }
                    messageAdapter=new MessagesAdapter(ChatActivity.this,mMensaje);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
