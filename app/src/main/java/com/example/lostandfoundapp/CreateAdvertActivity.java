package com.example.lostandfoundapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    RadioButton radioLost, radioFound;
    Spinner spinnerCategory;
    EditText editName, editPhone, editDescription, editDate, editLocation;
    Button btnChooseImage, btnSaveAdvert;
    ImageView imagePreview;

    DatabaseHelper databaseHelper;
    Uri selectedImageUri = null;

    String[] categories = {
            "Electronics",
            "Pets",
            "Wallets",
            "Keys",
            "Documents",
            "Clothing",
            "Other"
    };

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        databaseHelper = new DatabaseHelper(this);

        radioLost = findViewById(R.id.radioLost);
        radioFound = findViewById(R.id.radioFound);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editDescription = findViewById(R.id.editDescription);
        editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSaveAdvert = findViewById(R.id.btnSaveAdvert);
        imagePreview = findViewById(R.id.imagePreview);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                        try {
                            getContentResolver().takePersistableUriPermission(
                                    selectedImageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        imagePreview.setImageURI(selectedImageUri);
                    }
                }
        );

        btnChooseImage.setOnClickListener(v -> openImagePicker());

        btnSaveAdvert.setOnClickListener(v -> saveAdvert());
    }

    private void openImagePicker() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        imagePickerLauncher.launch(intent);
    }

    private void saveAdvert() {

        String postType;

        if (radioLost.isChecked()) {
            postType = "Lost";
        } else {
            postType = "Found";
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdAt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        boolean inserted = databaseHelper.insertAdvert(
                postType,
                category,
                name,
                phone,
                description,
                date,
                location,
                selectedImageUri.toString(),
                createdAt
        );

        if (inserted) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CreateAdvertActivity.this, ShowItemsActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
        }
    }
}