package com.Marche;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Marche.Fragmentos.InstruccionesActivity;
import com.google.common.escape.Escaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.ConnectException;

public class Marche extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marche);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                int bandera = Integer.parseInt(preferences.getString("bandera", "0"));

                if(bandera==1){
                    Intent intent = new Intent(Marche.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    editor.putString("bandera","1");
                    editor.commit();

                    Intent intent = new Intent(Marche.this, MainActivity.class);
                    startActivity(intent);
                    Intent intent2 = new Intent(Marche.this, InstruccionesActivity.class);
                    startActivity(intent2);

                }
                finish();
            }
        },3000);

    }



}
