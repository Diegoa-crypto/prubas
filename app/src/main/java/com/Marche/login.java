package com.Marche;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;

import io.opencensus.tags.Tag;

public class login extends AppCompatActivity {

   private EditText nombre, email, contrasena, telefono;
   private Button registrar, atras;
   private FirebaseAuth fAuth;
   private FirebaseFirestore fStore;
   private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nombre = (EditText) findViewById(R.id.txt_nnombre);
        registrar = (Button) findViewById(R.id.bt2_registro);
        atras = (Button) findViewById(R.id.bt2_regreso);
        email = (EditText) findViewById(R.id.txt_correo);
        contrasena = (EditText) findViewById(R.id.txt_contrasena);
        telefono = (EditText) findViewById(R.id.txt_telefono);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //if(fAuth.getCurrentUser() != null){
          //  startActivity((new Intent(getApplicationContext(),Menu.class)));
          //  finish();
       // }

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bea= email.getText().toString().trim();
                String contra = contrasena.getText().toString().trim();
                final String bnombre = nombre.getText().toString();
                final String btelefono = telefono.getText().toString();

                if(TextUtils.isEmpty(bea)){
                    email.setError("Se requiere correo");
                    return;
                }
                if(TextUtils.isEmpty(contra)){
                    contrasena.setError("Se requiere contraseña");
                    return;
                }

                if(contra.length()<6) {
                    contrasena.setError("Contraseña debe de ser mayor de 6 caracteres");
                    return;
                }
                //progressBar.setVisibility(View.VISIBLE);

                //metodo para registrar usuario

                fAuth.createUserWithEmailAndPassword(bea,contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(login.this,"Usuario creado", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("Usuarios").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", bnombre);
                            user.put("fTelefono", btelefono);
                            user.put("email", bea);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "onSuccess: Usuario creado con el ID "+userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Menu.class));

                        }else{
                            Toast.makeText(login.this,"Error¡ "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}
