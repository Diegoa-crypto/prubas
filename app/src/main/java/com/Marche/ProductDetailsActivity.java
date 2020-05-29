package com.Marche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.Marche.Perfil.Products;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productImage;
    private CircleImageView profileImageView;
    private TextView productName, productDescription, productprice, uName, uDate;
    private String PostKey;
    FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String porst_key="";
    private String user_id = "";
    private String user_name = "";
    private DatabaseReference ClickPostRef, db;
    LinearLayout linearLayout, send_user_info;
    BottomSheetBehavior bottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);



        PostKey = getIntent().getExtras().get("PostKey").toString();


        fStore = FirebaseFirestore.getInstance();

        productName =(TextView) findViewById(R.id.product_name_details);
        uName =(TextView) findViewById(R.id.post_profile_name);
        uDate =(TextView) findViewById(R.id.post_profile_desde);
        profileImageView =(CircleImageView) findViewById(R.id.image_details_user);
        productDescription =(TextView) findViewById(R.id.product_description_details);
        productprice =(TextView) findViewById(R.id.product_price_details);
        productImage=(ImageView) findViewById(R.id.product_image_details);
        linearLayout =(LinearLayout) findViewById(R.id.bottom_shet);
        send_user_info =(LinearLayout) findViewById(R.id.all_send_user);

        bottomSheetBehavior= BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);

        send_user_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                CharSequence options[] = new CharSequence[]
                        {
                                "Ver perfil",
                                "Enviar mensaje"

                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
                builder.setTitle("Selecciona una opcion");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0)
                        {
                            Intent profileintent = new Intent(ProductDetailsActivity.this, PersonProfileActivity.class);
                            profileintent.putExtra("user_id",user_id);
                            startActivity(profileintent);

                        }if(which==1){

                        Intent chatintent = new Intent(ProductDetailsActivity.this, InfoChatActivity.class);
                        chatintent.putExtra("user_id",user_id);
                        chatintent.putExtra("porst_key",porst_key);
                        chatintent.putExtra("userName", user_name);
                        startActivity(chatintent);

                    }

                    }
                });
                builder.show();

            }
        });



        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Products").child(PostKey);
        ClickPostRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);



                    uName.setText(products.getUserName());
                    productDescription.setText(products.getDescription());
                    productName.setText(products.getPname());
                    productprice.setText(products.getPrice());
                    //necesito agregar la fecha en que se registro el usuario

                    user_id=products.getUserid();
                    porst_key=products.getPid();
                    user_name=products.getUserName();

                    Picasso.get().load(products.getImage()).into(productImage);
                    Picasso.get().load(products.getUserImage()).into(profileImageView);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
