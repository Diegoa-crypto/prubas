package com.Marche;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.Marche.Fragmentos.InstruccionesActivity;

public class Marche extends AppCompatActivity {


    private static final String TAG ="info" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marche);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstTime()) {
                    Intent intent = new Intent(Marche.this, MainActivity.class);
                    startActivity(intent);
                    Intent intent2 = new Intent(Marche.this, InstruccionesActivity.class);
                    startActivity(intent2);
                }else{
                    Intent intent = new Intent(Marche.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }, 3000);

    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();

        }
        return !ranBefore;


    }
}
