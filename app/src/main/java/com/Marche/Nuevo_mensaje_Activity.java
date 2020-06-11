package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Marche.Perfil.Products;
import com.Marche.Perfil.Usuarios;
import com.Marche.ViewHolder.ProductAdapter;
import com.Marche.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Nuevo_mensaje_Activity extends AppCompatActivity {

    private RecyclerView recyclerView_nuevo_mensjae;
    private List<Usuarios> mUsuarios;
    private List<String> usuariosList;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID;
    DatabaseReference RootRef, ProductsRef;
    RecyclerView.LayoutManager layoutManager;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmendaje);

        recyclerView_nuevo_mensjae = findViewById(R.id.recycler_new_mensaje);
        recyclerView_nuevo_mensjae.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_nuevo_mensjae.setLayoutManager(layoutManager);

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        usuariosList=new ArrayList<>();

        RootRef= FirebaseDatabase.getInstance().getReference("Messages");
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariosList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Messages messages =snapshot.getValue(Messages.class);
                    if(messages.getFrom().equals(userID)){
                            usuariosList.add(messages.getPara());

                    }if(messages.getPara().equals(userID)){
                            usuariosList.add(messages.getFrom());

                    }

                }
                readChats();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readChats() {
        mUsuarios=new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();

        CollectionReference collectionReference=fStore.collection("Usuarios");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    Usuarios usuarios = documentSnapshot.toObject(Usuarios.class);
                    for (String id : usuariosList){
                        if(usuarios.getUserID().equals(id)){
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


    }


}
