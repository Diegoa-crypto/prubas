package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Marche.Perfil.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InfoChatActivity extends AppCompatActivity {
    private EditText fullNameEditText, userPhoneEditText, adressEditText, userMessageInput;
    private TextView name_product, precio_producto, desde_producto, close_info;
    private Button enviar_mensaje;
    private String messageReceicverID, messageSenderID,PostKey;
    private FirebaseAuth mAuth;

    private FirebaseFirestore fStore;
    private DatabaseReference RootRef, ClickPostRef;
    private String saveCurrentDate, saveCurrentTime;


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
        messageSenderID = mAuth.getCurrentUser().getUid();

        RootRef= FirebaseDatabase.getInstance().getReference();

        close_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


        PostKey=getIntent().getExtras().get("user_id").toString();
        messageReceicverID=getIntent().getExtras().get("porst_key").toString();
        fStore = FirebaseFirestore.getInstance();

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

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
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
            hashMap.put("message", messageText);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("date", saveCurrentDate);
            hashMap.put("type", "text");
            hashMap.put("from", messageSenderID);
            hashMap.put("para", PostKey);

            user_message_key.child("Messages").push().setValue(hashMap);


            user_message_key.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful()){
                        Toast.makeText(InfoChatActivity.this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                        finish();

                    }
                    else{
                        String message=task.getException().getMessage();
                        Toast.makeText(InfoChatActivity.this, "Error:  "+message, Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                        finish();
                    }


                }
            });
        }
    }

    private void userInfoDisplay(final EditText fullNameEditText, final EditText userPhoneEditText, final EditText adressEditText)
    {
        messageSenderID = mAuth.getCurrentUser().getUid();
        fStore.collection("Usuarios").document(messageSenderID).addSnapshotListener(new EventListener<DocumentSnapshot>()
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
}
