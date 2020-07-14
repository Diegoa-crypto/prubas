package com.Marche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.Marche.Notificaciones.Token;
import com.Marche.Perfil.Chatlist;
import com.Marche.Perfil.Usuarios;
import com.Marche.ViewHolder.ProductAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Nuevo_mensaje_Activity extends AppCompatActivity {

    private RecyclerView recyclerView_nuevo_mensjae;
    private List<Usuarios> mUsuarios;
    private List<Chatlist> usuariosList;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID;
    private DocumentReference Doc;
    private CircleImageView profile_image;
    FirebaseUser fuser;
    private static final String TAG = "";
    DatabaseReference RootRef;
    private String messageReceicverID;
    RecyclerView.LayoutManager layoutManager;
    private ProductAdapter productAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmendaje);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mensajes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView_nuevo_mensjae = findViewById(R.id.recycler_new_mensaje);
        recyclerView_nuevo_mensjae.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_nuevo_mensjae.setLayoutManager(layoutManager);

        //sharedPreferences = getPreferences(MODE_PRIVATE);
       // String pID= sharedPreferences.getString("porst_key","no hay nada");

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        usuariosList=new ArrayList<>();
        RootRef=FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariosList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Chatlist chatlist =snapshot.getValue(Chatlist.class);
                    usuariosList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());

        /*

        RootRef= FirebaseDatabase.getInstance().getReference("Messages");
        RootRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariosList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Messages messages =snapshot.getValue(Messages.class);
                    if(messages.getFrom().equals(fuser.getUid()))
                    {
                        //if(!usuariosList.contains(messages.getPara())){
                            usuariosList.add(messages.getPara());
                        //}


                    }if(messages.getPara().equals(fuser.getUid()))
                    {
                        //if(!usuariosList.contains(messages.getFrom())){
                            usuariosList.add(messages.getFrom());
                        //}

                    }

                }
                readChats();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */


    }
    private void updateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void chatList() {
        mUsuarios=new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference=fStore.collection("Usuarios");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mUsuarios.clear();
                for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                {
                    Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                    for(Chatlist chatlist:usuariosList){
                        if(usuarios.getUserID().equals(chatlist.getId())){
                            mUsuarios.add(usuarios);
                        }
                    }

                }
                productAdapter =new ProductAdapter(Nuevo_mensaje_Activity.this,mUsuarios,true);
                recyclerView_nuevo_mensjae.setAdapter(productAdapter);

            }
        });


    }
/*
    private void readChats()
    {
        mUsuarios=new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();


        CollectionReference collectionReference=fStore.collection("Usuarios");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                    {
                        Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                        for (String id : usuariosList)
                        {
                            if(usuarios.getUserID().equals(id))
                            {
                                if(mUsuarios.size() != 0)
                                {

                                    //for(Usuarios usuarios1: mUsuarios)
                                    //{
                                       // if(!usuarios.getUserID().equals(usuarios1.getUserID()))
                                       // {
                                            mUsuarios.add(usuarios);


                                       // }
                                    //}
                                }else {

                                    mUsuarios.add(usuarios);


                                }
                            }
                        }
                    }
                    productAdapter =new ProductAdapter(Nuevo_mensaje_Activity.this,mUsuarios);
                    recyclerView_nuevo_mensjae.setAdapter(productAdapter);

                }

            }
        });

                /*
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                    {
                        Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                        for (String id : usuariosList)
                        {
                            if(usuarios.getUserID().equals(id))
                            {
                                if(mUsuarios.size() != 0)
                                {
                                    for(Usuarios usuarios1: mUsuarios)
                                    {
                                        if(!usuarios.getUserID().equals(usuarios1.getUserID()))
                                        {
                                            mUsuarios.add(usuarios);
                                            break;

                                        }
                                    }
                                }else {
                                    mUsuarios.add(usuarios);
                                    break;

                                }
                            }
                        }
                    }
                    productAdapter =new ProductAdapter(Nuevo_mensaje_Activity.this,mUsuarios);
                    recyclerView_nuevo_mensjae.setAdapter(productAdapter);

                }

            }
        });
        */
                /*
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               // for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);

                    for (String id : usuariosList)
                    {
                        if(usuarios.getUserID().equals(id))
                        {
                            if(mUsuarios.size() != 0)
                            {
                                for(Usuarios usuarios1: mUsuarios)
                                {
                                    if(!usuarios.getUserID().equals(usuarios1.getUserID()))
                                    {
                                        mUsuarios.add(usuarios);
                                        break;

                                    }
                                }
                            }else {
                                mUsuarios.add(usuarios);
                                break;

                            }
                        }
                    }
                }
                productAdapter =new ProductAdapter(Nuevo_mensaje_Activity.this,mUsuarios);
                recyclerView_nuevo_mensjae.setAdapter(productAdapter);

            }
        });
        */



    //}


}
