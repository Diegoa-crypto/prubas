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

import com.Marche.Notificaciones.APIService;
import com.Marche.Notificaciones.Cliente;
import com.Marche.Notificaciones.Datos;
import com.Marche.Notificaciones.MyRespuesta;
import com.Marche.Notificaciones.Sender;
import com.Marche.Notificaciones.Token;
import com.Marche.Perfil.Products;
import com.Marche.Perfil.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChattoolBar;

    private ImageButton SendMessageButton;
    private EditText userMessageInput;

    private String messageReceicverID, messageReceiverImage, messageSenderID;
    private DatabaseReference RootRef;
    private DocumentReference Doc;
    private String saveCurrentDate, saveCurrentTime;

    private FirebaseAuth mAuth;



    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messageAdapter;
    private TextView username;
    FirebaseUser fuser;
    private CircleImageView receiverProfileImage;
    private SharedPreferences sharedPreferences;



    Intent intent;

    ValueEventListener seenListener;

    private FirebaseFirestore fStore;

    List<Messages> mMensaje;

    RecyclerView recyclerView;

    APIService apiService;

    boolean notify=false;


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
                startActivity(new Intent(ChatActivity.this,Nuevo_mensaje_Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                    String uimage= documentSnapshot.getString("image");
                    username.setText(name);
                    Picasso.get().load(uimage).placeholder(R.drawable.nini).into(receiverProfileImage);
                }

            }
        });
        seendMessage(messageReceicverID);
        apiService= Cliente.getCliente("https://fcm.googleapis.com/").create(APIService.class);






        SendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                notify=true;
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
    private void seendMessage(final String userid)
    {
        RootRef=FirebaseDatabase.getInstance().getReference("Messages");
        seenListener=RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Messages messages=snapshot.getValue(Messages.class);
                    if(messages.getPara().equals(fuser.getUid())&&messages.getFrom().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("visto",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(String from, final String para, String message)
    {

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("visto", false);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("type", "text");
        hashMap.put("from", from);
        hashMap.put("para", para);


        reference.child("Messages").push().setValue(hashMap);


        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(messageReceicverID)
                .child(fuser.getUid());
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(fuser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String msg=message;
        fStore.collection("Usuarios").document(fuser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                if(notify) {
                    sendNotification(para, usuarios.getfName(), msg);
                }
                notify=false;
            }
        });
    }
    private void currentUser(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userId);
        editor.apply();
    }
    private void status(String status) {
        Doc = fStore.collection("Users").document(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        Doc.update(hashMap);
    }
    private void sendNotification(String receiver, final String username, final String message)
    {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token=snapshot.getValue(Token.class);
                    Datos datos=new Datos(fuser.getUid(),R.drawable.logo_peque,username+": "+message, "Nuevo Mensaje",messageReceicverID);

                    Sender sender=new Sender(datos, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyRespuesta>() {
                                @Override
                                public void onResponse(Call<MyRespuesta> call, Response<MyRespuesta> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            Toast.makeText(ChatActivity.this, "!Fallo¡", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyRespuesta> call, Throwable t) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void readMessage(final String myid, final String userid)
    {
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


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(messageReceicverID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        RootRef.removeEventListener(seenListener);
        currentUser("none");
    }
}
