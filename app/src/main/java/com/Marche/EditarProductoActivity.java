package com.Marche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditarProductoActivity extends AppCompatActivity {

    private Button aplicarCambiosBtn;
    private EditText name, price, descripcion;
    private ImageView imageView;
    private String productID="";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        aplicarCambiosBtn=findViewById(R.id.edit_product);
        name=findViewById(R.id.product_name_edit);
        price=findViewById(R.id.product_price_edit);
        descripcion=findViewById(R.id.product_description_edit);
        imageView=findViewById(R.id.image_product_edit);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Editar producto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productID=getIntent().getStringExtra("pid");
        productRef= FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        displaySpecificInfo();

        aplicarCambiosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChange();
            }
        });


    }

    private void applyChange()
    {
        String pname=name.getText().toString();
        String pprice=price.getText().toString();
        String pdescription=descripcion.getText().toString();

        if(pname.equals("")){
            Toast.makeText(this, "Necesitas escribir el Nombre del Producto", Toast.LENGTH_SHORT).show();

        }else if(pprice.equals("")){
            Toast.makeText(this, "Necesitas escribir el Precio del Producto", Toast.LENGTH_SHORT).show();

        }else if(pdescription.equals("")){
            Toast.makeText(this, "Necesitas escribir la Descripcion del Producto", Toast.LENGTH_SHORT).show();
            
        }else{
            HashMap<String,Object> productMap=new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("pname",pname);
            productMap.put("price",pprice);
            productMap.put("description",pdescription);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(EditarProductoActivity.this, "Cambios aplicados", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(EditarProductoActivity.this,Mis_anuncios.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });


        }


    }

    private void displaySpecificInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists()){
                    String pname = snapshot.child("pname").getValue().toString();
                    String pprice = snapshot.child("price").getValue().toString();
                    String pdescription = snapshot.child("description").getValue().toString();
                    String pimage = snapshot.child("image").getValue().toString();

                    name.setText(pname);
                    price.setText(pprice);
                    descripcion.setText(pdescription);
                    Picasso.get().load(pimage).into(imageView);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
