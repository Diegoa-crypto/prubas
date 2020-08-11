package com.Marche;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.Marche.Notificaciones.Token;
import com.Marche.Perfil.Products;
import com.Marche.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseUser currentUser;
    FirebaseAuth fAuth;
    FirebaseUser fuser;
    //ImageButton moreBoton;
    FirebaseFirestore fStore;
    private DocumentReference Doc;
    private DatabaseReference ProductsRef,Nu;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private static final String TAG = "hola";
    private String usuarioID;
    TextView notification_in;
    String myUid;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Intent intent = new Intent(this, Nuevo_mensaje_Activity.class);
            startActivity(intent);
        }

       /* if (getIntent().getExtras() != null) {
            // Call your NotificationActivity here..
            Intent intent = new Intent(MenuActivity.this, Nuevo_mensaje_Activity.class);
            startActivity(intent);
        }*/

/*
        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();
            // dataBundle contains the notification data payload
            Intent intent  = new Intent(this, ChatActivity.class);
            intent.putExtra("userid",dataBundle);
            startActivity(intent);
        }*/

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        myUid=fuser.getUid();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        updateToken(FirebaseInstanceId.getInstance().getToken());

        Nu=FirebaseDatabase.getInstance().getReference("Messages");
        Nu.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Menu menu = navigationView.getMenu();
                int unread=0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    if(messages.getPara().equals(fuser.getUid())&&!messages.isVisto()){
                        unread++;
                    }

                }
                if(unread==0){
                    MenuItem nav_camara = menu.findItem(R.id.nav_mensajes);
                    View view=nav_camara.getActionView();
                    notification_in = (TextView) view.findViewById(R.id.number);
                    notification_in.setText("");
                    //String num=String.valueOf(unread);
                    /*if (view instanceof TextView) { // Double check, returned view is TextView
                        ((TextView) view).setText(unread);
                    }*/
                }else{
                    MenuItem nav_camara = menu.findItem(R.id.nav_mensajes);
                    View view=nav_camara.getActionView();
                    notification_in = (TextView) view.findViewById(R.id.number);
                    notification_in.setText(String.valueOf(unread));
                    //String num=String.valueOf(unread);
                    /*if (view instanceof TextView) {// Double check, returned view is TextView
                        ((TextView) view).setText(unread);
                    }*/
                    //nav_camara.setTitle("("+ unread + ")Mensjaes");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void firebaseSearch(String searchText)
    {
        Query firebaseSearchQuery=ProductsRef.orderByChild("pname").startAt(searchText).endAt(searchText+"\uf8ff");
        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(firebaseSearchQuery, Products.class)
                        .build();
        FirebaseRecyclerAdapter<Products,ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Products products) {
                        final String PostKey = getRef(i).getKey();


                        holder.txtproductName.setText(products.getPname());
                        holder.txtProductDescription.setText(products.getDescription());
                        holder.txtProductPrice.setText("Precio = "+ products.getPrice()+ "$");
                        Picasso.get().load(products.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MenuActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("PostKey", PostKey);
                                startActivity(intent);
                            }
                        });


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

    private void updateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    @Override
    protected void onStart()
    {
        Query SortPostsInDecendingOrder = ProductsRef.orderByChild("counter");
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
        new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(SortPostsInDecendingOrder, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull Products model)
                    {
                        final String PostKey = getRef(position).getKey();
                        final String pid=model.getPid();
                        final String pimage=model.getImage();

                        holder.moreBoton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showMoreOptions(holder.moreBoton,PostKey,myUid,pid,pimage);
                            }
                        });


                        holder.txtproductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Precio = "+ model.getPrice()+ "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MenuActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("PostKey", PostKey);
                                startActivity(intent);
                            }
                        });


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

    private void showMoreOptions(ImageButton moreBoton, String postKey, String myUid, final String pid, final String pimage) {
        PopupMenu popupMenu=new PopupMenu(MenuActivity.this,moreBoton, Gravity.END);

        if(postKey.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Borrar");

        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==0){
                    beginDelete(pid,pimage);

                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(final String pid, String pimage) {
        //Progress bar
        final ProgressDialog pd = new ProgressDialog(MenuActivity.this);
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
                                Toast.makeText(MenuActivity.this, "Borrado exitosamente.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MenuActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    /*
    private void searchPosts(final String searchQuery)
    {
        Query SortPostsInDecendingOrder = ProductsRef.orderByChild("pname");
        //super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(SortPostsInDecendingOrder, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model)
                    {
                        final String PostKey = getRef(position).getKey();


                        holder.txtproductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Precio = "+ model.getPrice()+ "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MenuActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("PostKey", PostKey);
                                startActivity(intent);
                            }
                        });
                        if(model.getPname().toLowerCase().contains(searchQuery.toLowerCase())||model.getDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                            Query SortPostsInDecendingOrder = ProductsRef.orderByChild("pname");
                            //super.onStart();
                            FirebaseRecyclerOptions<Products> options =
                                    new FirebaseRecyclerOptions.Builder<Products>()
                                            .setQuery(SortPostsInDecendingOrder, Products.class)
                                            .build();

                            FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                                    new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options)
                                    {
                                        @Override
                                        protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model)
                                        {
                                            final String PostKey = getRef(position).getKey();


                                            holder.txtproductName.setText(model.getPname());
                                            holder.txtProductDescription.setText(model.getDescription());
                                            holder.txtProductPrice.setText("Precio = "+ model.getPrice()+ "$");
                                            Picasso.get().load(model.getImage()).into(holder.imageView);

                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(MenuActivity.this, ProductDetailsActivity.class);
                                                    intent.putExtra("PostKey", PostKey);
                                                    startActivity(intent);
                                                }
                                            });


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


                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        /*
        //SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if(!TextUtils.isEmpty(query))
                {
                    searchPosts(query);
                }
                else{
                    //falta el load
                    Query SortPostsInDecendingOrder = ProductsRef.orderByChild("counter");
                    //super.onStart();
                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(SortPostsInDecendingOrder, Products.class)
                                    .build();

                    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options)
                            {
                                @Override
                                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model)
                                {
                                    final String PostKey = getRef(position).getKey();


                                    holder.txtproductName.setText(model.getPname());
                                    holder.txtProductDescription.setText(model.getDescription());
                                    holder.txtProductPrice.setText("Precio = "+ model.getPrice()+ "$");
                                    Picasso.get().load(model.getImage()).into(holder.imageView);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(MenuActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("PostKey", PostKey);
                                            startActivity(intent);
                                        }
                                    });


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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText))
                {
                    searchPosts(newText);
                }
                else{
                    //falta el load
                    Query SortPostsInDecendingOrder = ProductsRef.orderByChild("counter");
                    //super.onStart();
                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(SortPostsInDecendingOrder, Products.class)
                                    .build();

                    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options)
                            {
                                @Override
                                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model)
                                {
                                    final String PostKey = getRef(position).getKey();


                                    holder.txtproductName.setText(model.getPname());
                                    holder.txtProductDescription.setText(model.getDescription());
                                    holder.txtProductPrice.setText("Precio = "+ model.getPrice()+ "$");
                                    Picasso.get().load(model.getImage()).into(holder.imageView);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(MenuActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("PostKey", PostKey);
                                            startActivity(intent);
                                        }
                                    });


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
                return false;
            }
        });*/
        //super.onCreateOptionsMenu(menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
    /*   if(id==R.id.action_settings){
            return true;
        }*/


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
       int id = item.getItemId();
        if (id == R.id.nav_orders)
        {

        }
        else if (id == R.id.nav_categories)
        {
            Intent intent = new Intent(getApplicationContext(), Mis_anuncios.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_tools)
        {
            Intent intent = new Intent(MenuActivity.this, SettinsActivity.class);
            startActivity(intent);
            finish();

        }
        else if (id == R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();

        }
        else if (id == R.id.nav_perfil)
        {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            finish();
        }else if (id == R.id.nav_mensajes)
        {
            startActivity(new Intent(getApplicationContext(), Nuevo_mensaje_Activity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void updateNavHeader(){
        usuarioID = fAuth.getCurrentUser().getUid();
        fStore.collection("Usuarios").document(usuarioID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    Log.w(TAG, "Error: ", e);
                    return;
                }if(documentSnapshot!=null){
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    TextView UserNameTextView = headerView.findViewById(R.id.user_profile_name);
                    CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

                    UserNameTextView.setText(documentSnapshot.getString("fName"));
                    Picasso.get().load(documentSnapshot.getString("image")).placeholder(R.drawable.profile).into(profileImageView);

                }else{
                    Log.d(TAG,"Sin data");
                }
            }
        });



    }
    private void status(String status){

        Doc= fStore.collection("Usuarios").document(fuser.getUid());

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        Doc.update(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
