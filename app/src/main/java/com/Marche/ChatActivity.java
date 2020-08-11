package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Marche.Notificaciones.APIService;
import com.Marche.Notificaciones.Client;
import com.Marche.Notificaciones.Data;
import com.Marche.Notificaciones.MyResponse;
import com.Marche.Notificaciones.Sender;
import com.Marche.Notificaciones.Token;
import com.Marche.Perfil.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChattoolBar;

    private ImageButton SendMessageButton;
    private EditText userMessageInput;

    private String messageReceicverID, messageReceiverpname, messageSenderID, pprice_mensaje, pimage_mensaje;
    private DatabaseReference RootRef;
    private DocumentReference Doc;
    private String saveCurrentDate, saveCurrentTime;

    private FirebaseAuth mAuth;



    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messageAdapter;
    private TextView username,pname_mensaje, price_product;
    FirebaseUser fuser;
    private CircleImageView receiverProductImage;
    private SharedPreferences sharedPreferences;



    Intent intent;

    ValueEventListener seenListener;

    private FirebaseFirestore fStore;

    List<Messages> mMensaje;

    RecyclerView recyclerView;

    APIService apiService;

    SharedPreferences sharedPref;

    boolean notify=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /*
        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();
            // dataBundle contains the notification data payload
            Intent intent  = new Intent(this, ChatActivity.class);
            intent.putExtra("userid",dataBundle);
            startActivity(intent);
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //quite un setdsplayshow
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,Nuevo_mensaje_Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();//agregeu este finishporque se colgaba la aplicacion
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
        pname_mensaje=(TextView) findViewById(R.id.nombre_producto_info_mensage);
        price_product=(TextView) findViewById(R.id.precio_producto_info_mensage);
        receiverProductImage=(CircleImageView) findViewById(R.id.product_mensajes);

        RootRef= FirebaseDatabase.getInstance().getReference();

        mAuth= FirebaseAuth.getInstance();

        fuser= FirebaseAuth.getInstance().getCurrentUser();

        messageSenderID=mAuth.getCurrentUser().getUid();

        fStore = FirebaseFirestore.getInstance();

        intent = getIntent();

        messageReceicverID=intent.getStringExtra("userid");
        messageReceiverpname=intent.getStringExtra("pname_mesage");
        pprice_mensaje=intent.getStringExtra("pprice_mensaje");
        pimage_mensaje=intent.getStringExtra("pimage_mensaje");

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        sharedPref.edit().putString("pname_id", messageReceiverpname).commit();
        sharedPref.edit().putString("pprice", pprice_mensaje).commit();
        sharedPref.edit().putString("ppimage", pimage_mensaje).commit();

        String pimage = sharedPref.getString("ppimage", "nada");
        String pprice = sharedPref.getString("pprice", "nada");
        String userId = sharedPref.getString("pname_id", "nada");

        Picasso.get().load(pimage).placeholder(R.drawable.nini).into(receiverProductImage);
        price_product.setText(pprice);
        pname_mensaje.setText(userId);
        //messageReceiverName=getIntent().getExtras().get("userName").toString();

        fStore.collection("Usuarios").document(messageReceicverID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null && documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("fName");
                    String uimage= documentSnapshot.getString("image");
                    username.setText(name);
                }

            }
        });
        seendMessage(messageReceicverID);
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);






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
        //hashMap.put("deleted", "");


        reference.child("Messages").push().setValue(hashMap);


        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(messageReceicverID)
                .child(fuser.getUid());
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(fuser.getUid());
                    chatRef.child("deleted").setValue("");
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
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if (documentSnapshot != null)
                {
                    Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                    if(notify) {
                        sendNotification(messageReceicverID, usuarios.getfName(), msg);
                    }
                    notify=false;
                }
            }
        });
    }
    private void currentUser(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userId);
        editor.apply();
    }
    private void status(String status) {
        Doc = fStore.collection("Usuarios").document(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        Doc.update(hashMap);
    }
    private void sendNotification(String receiver, final String username, final String message)
    {
        final String click_action = "NOTIFICATIONACTIVITY";
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token=snapshot.getValue(Token.class);
                    Data data =new Data(fuser.getUid(),R.drawable.logo_peque,username+": "+
                            message, "Nuevo Mensaje",messageReceicverID,click_action);

                    Sender sender=new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            Toast.makeText(ChatActivity.this, "!Fallo¡", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

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

                        /*if (messages.getDeleted().equals(myid)) {
                            mMensaje.clear();
                        }
                        if (messages.getDeleted().length() > 0) {
                            snapshot.getRef().removeValue();

                        }*/

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
