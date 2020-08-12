package com.Marche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.Marche.Perfil.Products;
import com.Marche.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Mis_anuncios extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String myUid;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_anuncios);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        myUid=fuser.getUid();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis anuncios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_anuncios);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Products");
        Query query = reference.orderByChild("userid").equalTo(myUid);
        super.onStart();
        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(query, Products.class)
                        .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int i, @NonNull Products products)
                    {
                        final String PostKey = getRef(i).getKey();
                        final String pid=products.getPid();
                        final String userid_product=products.getUserid();
                        final String pimage=products.getImage();

                        holder.moreBoton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showMoreOptions(holder.moreBoton,userid_product,myUid,pid,pimage);
                            }
                        });

                        holder.txtproductName.setText(products.getPname());
                        holder.txtProductDescription.setText(products.getDescription());
                        holder.txtProductPrice.setText("Precio = "+ products.getPrice()+ "$");
                        Picasso.get().load(products.getImage()).into(holder.imageView);


                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void showMoreOptions(ImageButton moreBoton, String userid_product, String myUid, final String pid, final String pimage) 
    {
        PopupMenu popupMenu=new PopupMenu(Mis_anuncios.this,moreBoton, Gravity.END);

        if(userid_product.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Vendido");
            popupMenu.getMenu().add(Menu.NONE,1,0,"Editar");

        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==0){
                    beginDelete(pid,pimage);

                }else if(id==1){
                    Intent intent=new Intent(Mis_anuncios.this,EditarProductoActivity.class);
                    intent.putExtra("pid",pid);
                    startActivity(intent);


                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(final String pid, String pimage)
    {
        final ProgressDialog pd = new ProgressDialog(Mis_anuncios.this);
        pd.setMessage("Borrando....");
        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(pimage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Query fquery=FirebaseDatabase.getInstance().getReference("Products").orderByChild("pid").equalTo(pid);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(Mis_anuncios.this, "Borrado exitosamente.", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Mis_anuncios.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
