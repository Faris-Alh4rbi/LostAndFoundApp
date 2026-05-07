package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowItemsActivity extends AppCompatActivity {

    Spinner spinnerFilterCategory;
    Button btnSearchCategory, btnShowAll;
    ListView listViewAdverts;

    DatabaseHelper databaseHelper;
    ArrayList<Advert> advertList;
    AdvertAdapter advertAdapter;

    String[] categories = {
            "Electronics",
            "Pets",
            "Wallets",
            "Keys",
            "Documents",
            "Clothing",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        spinnerFilterCategory = findViewById(R.id.spinnerFilterCategory);
        btnSearchCategory = findViewById(R.id.btnSearchCategory);
        btnShowAll = findViewById(R.id.btnShowAll);
        listViewAdverts = findViewById(R.id.listViewAdverts);

        databaseHelper = new DatabaseHelper(this);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterCategory.setAdapter(spinnerAdapter);

        loadAllAdverts();

        btnSearchCategory.setOnClickListener(v -> {
            String selectedCategory = spinnerFilterCategory.getSelectedItem().toString();
            advertList = databaseHelper.getAdvertsByCategory(selectedCategory);

            advertAdapter = new AdvertAdapter(ShowItemsActivity.this, advertList);
            listViewAdverts.setAdapter(advertAdapter);

            if (advertList.isEmpty()) {
                Toast.makeText(this, "No items found in this category", Toast.LENGTH_SHORT).show();
            }
        });

        btnShowAll.setOnClickListener(v -> loadAllAdverts());

        listViewAdverts.setOnItemClickListener((parent, view, position, id) -> {
            Advert selectedAdvert = advertList.get(position);

            Intent intent = new Intent(ShowItemsActivity.this, ItemDetailActivity.class);
            intent.putExtra("advert_id", selectedAdvert.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllAdverts();
    }

    private void loadAllAdverts() {
        advertList = databaseHelper.getAllAdverts();
        advertAdapter = new AdvertAdapter(this, advertList);
        listViewAdverts.setAdapter(advertAdapter);

        if (advertList.isEmpty()) {
            Toast.makeText(this, "No adverts saved yet", Toast.LENGTH_SHORT).show();
        }
    }
}