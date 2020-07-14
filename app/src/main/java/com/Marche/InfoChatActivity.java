package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Marche.Notificaciones.APIService;
import com.Marche.Notificaciones.Client;
import com.Marche.Notificaciones.Data;
import com.Marche.Notificaciones.MyResponse;
import com.Marche.Notificaciones.Sender;
import com.Marche.Notificaciones.Token;
import com.Marche.Perfil.Products;
import com.Marche.Perfil.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoChatActivity extends AppCompatActivity {
    private EditText fullNameEditText, userPhoneEditText, adressEditText, userMessageInput;
    private TextView name_product, precio_producto, desde_producto, close_info;
    private Button enviar_mensaje;
    private String messageReceicverID,PostKey;
    private FirebaseAuth mAuth;
    private DocumentReference Doc;
    private String pname, pimage;
    private SharedPreferences sharedPreferences;


    private FirebaseFirestore fStore;
    private DatabaseReference RootRef, ClickPostRef;
    private String saveCurrentDate, saveCurrentTime;
    FirebaseUser fuser;
    APIService apiService;

    ValueEventListener seenListener;

    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_chat);

        fullNameEditText= (EditText) findViewById(R.id.info_phone_number);
        userPhoneEditText= (EditText) findViewById(R.id.info_full_name);
        adressEditText= (EditText) findViewById(R.id.info_address);
        userMessageInput= (EditText) findViewById(R.id.info_mensaje);
        name_product=(TextView) findViewById(R.id.nombre_producto_info);
        precio_producto=(TextView) findViewById(R.id.precio_producto_info);
        desde_producto=(TextView) findViewById(R.id.desde_producto_info);
        enviar_mensaje=(Button) findViewById(R.id.enviar_user_info);
        close_info = (TextView) findViewById(R.id.close_info_btn);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fuser= FirebaseAuth.getInstance().getCurrentUser();


        RootRef= FirebaseDatabase.getInstance().getReference();

        close_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(InfoChatActivity.this,MenuActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


        PostKey=getIntent().getExtras().get("user_id").toString();
        messageReceicverID=getIntent().getExtras().get("porst_key").toString();
        fStore = FirebaseFirestore.getInstance();

        seendMessage(PostKey);

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        userInfoDisplay(fullNameEditText, userPhoneEditText, adressEditText);

        //crear metodo para mostrar informacion del producto como en el activity product details
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Products").child(messageReceicverID);
        ClickPostRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);


                    name_product.setText(products.getPname());
                    precio_producto.setText(products.getPrice());
                    pname=products.getPname();
                    pimage=products.getImage();
/*
                    sharedPreferences = getSharedPreferences("nombre", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("pname",pname);
                    editor.commit();

 */

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                sendMessage();
            }
        });


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

    private void sendMessage()
    {
        String messageText = userMessageInput.getText().toString();
        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Necesitas escribir un mensaje....", Toast.LENGTH_SHORT).show();

        }else{
            DatabaseReference user_message_key=FirebaseDatabase.getInstance().getReference();


            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(calFordDate.getTime());



            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("pname",pname);
            hashMap.put("visto", false);
            hashMap.put("pimage",pimage);
            hashMap.put("message", messageText);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("date", saveCurrentDate);
            hashMap.put("type", "text");
            hashMap.put("from", fuser.getUid());
            hashMap.put("para", PostKey);
            user_message_key.child("Messages").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful()){

                        Toast.makeText(InfoChatActivity.this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                        startActivity(new Intent(InfoChatActivity.this, MenuActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                    else{
                        String message=task.getException().getMessage();
                        Toast.makeText(InfoChatActivity.this, "Error:  "+message, Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                        startActivity(new Intent(InfoChatActivity.this, MenuActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }


                }
            });

            final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(PostKey)
                    .child(fuser.getUid());
            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(!dataSnapshot.exists()){
                        chatRef.child("id").setValue(fuser.getUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final String msg=messageText;
            fStore.collection("Usuarios").document(fuser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>()
            {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
                {
                    Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                    if(notify) {
                        sendNotification(PostKey, usuarios.getfName(), msg);
                    }
                    notify=false;
                }
            });

                    /*
                    .addSnapshotListener(new EventListener<DocumentSnapshot>()
            {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    assert documentSnapshot != null;
                    Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                    if(notify) {
                        assert usuarios != null;
                        sendNotification(PostKey, usuarios.getfName(), msg);
                    }
                    notify=false;
                }
            });
            */




        }
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
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token=snapshot.getValue(Token.class);
                    Data data =new Data(fuser.getUid(),R.drawable.logo_peque,username+": "+message, "Nuevo Mensaje",PostKey);

                    Sender sender=new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            Toast.makeText(InfoChatActivity.this, "!Fallo¡", Toast.LENGTH_SHORT).show();
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

    private void userInfoDisplay(final EditText fullNameEditText, final EditText userPhoneEditText, final EditText adressEditText)
    {

        fStore.collection("Usuarios").document(fuser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(documentSnapshot!=null && documentSnapshot.exists()) {

                    String name = documentSnapshot.getString("fName");
                    String phone = documentSnapshot.getString("fTelefono");
                    String address = documentSnapshot.getString("email");
                    fullNameEditText.setText(name);
                    userPhoneEditText.setText(phone);
                    adressEditText.setText(address);


                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(PostKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        RootRef.removeEventListener(seenListener);
        currentUser("none");
    }
}
