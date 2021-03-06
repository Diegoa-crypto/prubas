package com.Marche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String pname="";
    private String pimage="";
    private String user_id = "";
    private String user_name = "";
    private String phone_id = "";
    private static final int REQUEST_CALL=1;
    private Button enviar_mensaje, llamar_usuario;
    private DatabaseReference ClickPostRef, db;
    LinearLayout linearLayout, send_user_info;
    BottomSheetBehavior bottomSheetBehavior;
    private SharedPreferences sharedPreferences;


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
        enviar_mensaje=(Button) findViewById(R.id.mensaje_user);
        llamar_usuario=(Button) findViewById(R.id.llamar_user);

        bottomSheetBehavior= BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);

        enviar_mensaje.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent chatintent2 = new Intent(ProductDetailsActivity.this, InfoChatActivity.class);
                chatintent2.putExtra("user_id",user_id);
                chatintent2.putExtra("porst_key",porst_key);
                chatintent2.putExtra("userName", user_name);
                chatintent2.putExtra("pname", pname);

                startActivity(chatintent2);
            }
        });
        llamar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });



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
                        chatintent.putExtra("userid",user_id);
                        chatintent.putExtra("porst_key",porst_key);
                        chatintent.putExtra("userName", user_name);
                        chatintent.putExtra("pname", pname);
                        chatintent.putExtra("pimage", pimage);
                        startActivity(chatintent);

                        //sharedPreferences = getPreferences(MODE_PRIVATE);
                        //SharedPreferences.Editor editor = sharedPreferences.edit();
                        //editor.putString("porst_key", porst_key);
                        //editor.commit();

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

                    uDate.setText(products.getUserDate());
                    uName.setText(products.getUserName());
                    productDescription.setText(products.getDescription());
                    productName.setText(products.getPname());
                    productprice.setText(products.getPrice());


                    user_id=products.getUserid();
                    porst_key=products.getPid();
                    pname=products.getPname();
                    user_name=products.getUserName();
                    phone_id=products.getUserphone();
                    pimage=products.getImage();

                    Picasso.get().load(products.getImage()).into(productImage);
                    Picasso.get().load(products.getUserImage()).placeholder(R.drawable.profile).into(profileImageView);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void makePhoneCall() {
        if(phone_id != null) {

            if (ContextCompat.checkSelfPermission(ProductDetailsActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProductDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

            }else {
                Intent intent= new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phone_id,null));
                startActivity(intent);
            }
        }else{
            Toast.makeText(this, "No tiene numero registrado", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CALL){
            if(grantResults.length>0&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else {
                Toast.makeText(this,"Permiso denegado",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
