package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnCreateAdvert, btnShowItems, btnShowMap;
    EditText editRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        btnShowItems = findViewById(R.id.btnShowItems);
        btnShowMap = findViewById(R.id.btnShowMap);
        editRadius = findViewById(R.id.editRadius);

        btnCreateAdvert.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
            startActivity(intent);
        });

        btnShowItems.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShowItemsActivity.class);
            startActivity(intent);
        });

        btnShowMap.setOnClickListener(v -> {

            float radiusKm = 10;

            String radiusText = editRadius.getText().toString().trim();

            if (!radiusText.isEmpty()) {
                try {
                    radiusKm = Float.parseFloat(radiusText);
                } catch (Exception e) {
                    Toast.makeText(this, "Invalid radius. Using 10 km.", Toast.LENGTH_SHORT).show();
                    radiusKm = 10;
                }
            }

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("radiusKm", radiusKm);
            startActivity(intent);
        });
    }
}