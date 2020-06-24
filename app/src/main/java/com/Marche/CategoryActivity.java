package com.Marche;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CategoryActivity extends AppCompatActivity
{
    private ImageView Derivados, Frutos, Verduras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Publica un alimento");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Derivados = (ImageView) findViewById(R.id.derivados);
        Frutos = (ImageView) findViewById(R.id.frutos);
        Verduras = (ImageView) findViewById(R.id.verduras);

        Derivados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CategoryActivity.this , addnewcategoryActivity.class);
                intent.putExtra("category", "Derivados");
                startActivity(intent);
            }
        });

        Frutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CategoryActivity.this , addnewcategoryActivity.class);
                intent.putExtra("category", "Frutos");
                startActivity(intent);
            }
        });

        Verduras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CategoryActivity.this , addnewcategoryActivity.class);
                intent.putExtra("category", "Verduras");
                startActivity(intent);
            }
        });


    }

}
