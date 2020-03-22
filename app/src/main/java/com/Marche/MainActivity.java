package com.Marche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
   EditText email;
   EditText contrasena;
   Button bin;
   Button bregis;
   FirebaseAuth fAuth;
   TextView forgotTextLink;
   private FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicialisamos el objeto firebase
        fAuth = FirebaseAuth.getInstance();

        //referenciamos los views
        bregis = (Button)  findViewById(R.id.b_nuevo);
        email = (EditText) findViewById(R.id.t_email);
        contrasena = (EditText) findViewById(R.id.t_contra);
        bin = (Button) findViewById(R.id.b_ingreso);
        forgotTextLink = findViewById(R.id.t_recuperar);

        bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emai= email.getText().toString().trim();
                String contra = contrasena.getText().toString().trim();

                if(TextUtils.isEmpty(emai)){
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
                //autentificacion de usuario

                fAuth.signInWithEmailAndPassword(emai,contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Inicio exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Menu.class));
                        }else{
                            Toast.makeText(MainActivity.this,"Error¡ "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });



            }
        });

        bregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder paswordResetDialog = new AlertDialog.Builder(v.getContext());
                paswordResetDialog.setTitle("Olvidaste tu contraseña");
                paswordResetDialog.setMessage("Ingresa tu correo para establecer tu contraseña");
                paswordResetDialog.setView(resetMail);

                paswordResetDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mail= resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Ingresa tu correo para establecer contraseña", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error¡ Link no enviado" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                paswordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //

                    }
                });
                paswordResetDialog.create().show();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        currentUser = fAuth.getCurrentUser();
        if(currentUser != null){
            startActivity((new Intent(getApplicationContext(), Menu.class)));
        }
    }
}
