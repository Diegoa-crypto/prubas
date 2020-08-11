package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.admin.SystemUpdatePolicy;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.validation.Validator;

import io.grpc.Context;

public class addnewcategoryActivity extends AppCompatActivity {
    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime, userID;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    FirebaseUser currentUser;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    private long countPost = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewcategory);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Publica un alimento");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CategoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        loadingBar = new ProgressDialog(this);

        AddNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDescription = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             //OpenGallery();
                CropImage.activity(ImageUri)
                        .setAspectRatio(1,1)
                        .start(addnewcategoryActivity.this);


            }
        });
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateProductData();
            }
        });


    }
/*
    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

 */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            ImageUri= result.getUri();

            InputProductImage.setImageURI(ImageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Intentalo de nuevo", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(addnewcategoryActivity.this, SettinsActivity.class));
            finish();
        }

        /*

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data != null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }

         */
    }

    private void ValidateProductData()
    {
        Description=InputProductDescription.getText().toString();
        Price=InputProductPrice.getText().toString();
        Pname=InputProductName.getText().toString();

        if(ImageUri==null)
        {
            Toast.makeText(this, "Necesitas agregar una Imagen", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"Escribe la descripcion del alimento", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,"Necesitas escribir un precio", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this,"Escribe el nombre del alimento", Toast.LENGTH_SHORT).show();
        }else
        {
            StoreProductInformation();
        }

    }
    private void  StoreProductInformation()
    {

        loadingBar.setTitle("Agregando Alimento");
        loadingBar.setMessage("Porfavor espere, agregando producto");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = fAuth.getCurrentUser().getUid();


        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey  + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(addnewcategoryActivity.this,"Error "+message, Toast.LENGTH_LONG).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(addnewcategoryActivity.this, "Imagen cargada con exito", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                     if (task.isSuccessful())
                     {
                         downloadImageUrl = task.getResult().toString();
                         Toast.makeText(addnewcategoryActivity.this, "La URL de la imagen se cargo correctamente", Toast.LENGTH_SHORT).show();
                         SaveProductInfoToDatabase();


                     }
                    }
                });


            }
        });




    }

    private void SaveProductInfoToDatabase()
    {
        ProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()){
                    countPost = dataSnapshot.getChildrenCount()*-1;

                }else{
                    countPost=0;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fStore.collection("Usuarios").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String userFullName = documentSnapshot.getString("fName");
                    String userphone= documentSnapshot.getString("fTelefono");
                    String userProfileImage = documentSnapshot.getString("image");
                    String userdate=documentSnapshot.getString("date");

                    HashMap <String, Object> productMap = new HashMap<>();
                    productMap.put("counter",countPost);
                    productMap.put("userDate",userdate);
                    productMap.put("userName", userFullName);
                    productMap.put("userphone", userphone);
                    productMap.put("userImage", userProfileImage);
                    productMap.put("userid", userID);
                    productMap.put("pid", productRandomKey);
                    productMap.put("date", saveCurrentDate);
                    productMap.put("time", saveCurrentTime);
                    productMap.put("description", Description);
                    productMap.put("image", downloadImageUrl);
                    productMap.put("category", CategoryName);
                    productMap.put("price", Price);
                    productMap.put("pname", Pname);

                    ProductsRef.child(productRandomKey).updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        //aqui otra vez lo regregasmo al activity de categorias
                                        Intent intent = new Intent(addnewcategoryActivity.this, CategoryActivity.class);
                                        startActivity(intent);

                                        loadingBar.dismiss();
                                        Toast.makeText(addnewcategoryActivity.this, "Producto agregado con exito", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(addnewcategoryActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }
                            });

                }

            }
        });




    }
}
