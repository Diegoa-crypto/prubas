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
import com.Marche.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Nuevo_mensaje_Activity extends AppCompatActivity {

    private RecyclerView recyclerView_nuevo_mensjae;
    private List<Products> mProducts;
    private List<String> productsList;
    private FirebaseAuth fAuth;
    private String userID;
    private DatabaseReference RootRef, ProductsRef;
    RecyclerView.LayoutManager layoutManager;

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

        productsList=new ArrayList<>();

        RootRef= FirebaseDatabase.getInstance().getReference("Messages");
        ProductsRef= FirebaseDatabase.getInstance().getReference("Products");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<Products>options=
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new
                FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Products products)
                    {
                        final String PostKey = getRef(i).getKey();

                        RootRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                productsList.clear();
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    Messages messages=snapshot.getValue(Messages.class);
                                    if(messages.getFrom().equals(userID)){
                                        productsList.add(messages.getPara());
                                    }
                                    if(messages.getPara().equals(userID)){
                                        productsList.add(messages.getFrom());
                                    }
                                }
                                //Aqui es para leer el me
                                mProducts= new ArrayList<>();

                                RootRef = FirebaseDatabase.getInstance().getReference("Products");

                                RootRef.addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        mProducts.clear();

                                        for (DataSnapshot snapshot: dataSnapshot.getChildren())
                                        {
                                            Products products = snapshot.getValue(Products.class);
                                            String key_producto=products.getPid();
                                            for(String id:productsList){
                                                if(mProducts.size() !=0){
                                                    for (Products products1:mProducts){
                                                        if(!products.getPid().equals(products1.getPid())){
                                                            mProducts.add(products);
                                                            break;
                                                        }
                                                    }
                                                }else{
                                                    mProducts.add(products);
                                                    break;
                                                }
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Nuevo_mensaje_Activity.this, ChatActivity.class);
                                intent.putExtra("user_id",PostKey);
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_mensaje_item, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView_nuevo_mensjae.setAdapter(adapter);
        adapter.startListening();




    }


}
