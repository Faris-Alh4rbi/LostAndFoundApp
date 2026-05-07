package com.example.lostandfoundapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {

    TextView textDetailTitle, textDetailName, textDetailPhone, textDetailDescription;
    TextView textDetailDate, textDetailLocation, textDetailCreatedAt;
    ImageView detailImage;
    Button btnRemoveAdvert;

    DatabaseHelper databaseHelper;
    Advert advert;
    int advertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        textDetailTitle = findViewById(R.id.textDetailTitle);
        textDetailName = findViewById(R.id.textDetailName);
        textDetailPhone = findViewById(R.id.textDetailPhone);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        textDetailDate = findViewById(R.id.textDetailDate);
        textDetailLocation = findViewById(R.id.textDetailLocation);
        textDetailCreatedAt = findViewById(R.id.textDetailCreatedAt);
        detailImage = findViewById(R.id.detailImage);
        btnRemoveAdvert = findViewById(R.id.btnRemoveAdvert);

        databaseHelper = new DatabaseHelper(this);

        advertId = getIntent().getIntExtra("advert_id", -1);

        if (advertId == -1) {
            Toast.makeText(this, "Advert not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        advert = databaseHelper.getAdvertById(advertId);

        if (advert == null) {
            Toast.makeText(this, "Advert not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayAdvert();

        btnRemoveAdvert.setOnClickListener(v -> {
            boolean deleted = databaseHelper.deleteAdvert(advertId);

            if (deleted) {
                Toast.makeText(this, "Advert removed successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Could not remove advert", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAdvert() {

        textDetailTitle.setText(advert.getPostType() + " - " + advert.getCategory());
        textDetailName.setText("Item Name: " + advert.getName());
        textDetailPhone.setText("Phone: " + advert.getPhone());
        textDetailDescription.setText("Description: " + advert.getDescription());
        textDetailDate.setText("Date Lost/Found: " + advert.getDate());
        textDetailLocation.setText("Location: " + advert.getLocation());
        textDetailCreatedAt.setText("Posted: " + advert.getCreatedAt());

        try {
            detailImage.setImageURI(Uri.parse(advert.getImageUri()));
        } catch (Exception e) {
            detailImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}