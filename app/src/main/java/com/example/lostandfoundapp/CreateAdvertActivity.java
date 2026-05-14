package com.example.lostandfoundapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 100;

    RadioButton radioLost, radioFound;
    Spinner spinnerCategory;
    EditText editName, editPhone, editDescription, editDate, editLocation;
    Button btnChooseImage, btnSaveAdvert, btnCurrentLocation;
    ImageView imagePreview;

    DatabaseHelper databaseHelper;
    Uri selectedImageUri = null;

    double advertLatitude = 0.0;
    double advertLongitude = 0.0;
    boolean locationChosen = false;

    FusedLocationProviderClient fusedLocationClient;

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
    ActivityResultLauncher<Intent> placePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.PLACES_API_KEY);
        }

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
        btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
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

        placePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Place place = Autocomplete.getPlaceFromIntent(result.getData());

                        if (place.getFormattedAddress() != null) {
                            editLocation.setText(place.getFormattedAddress());
                        } else {
                            editLocation.setText(place.getDisplayName());
                        }

                        if (place.getLocation() != null) {
                            advertLatitude = place.getLocation().latitude;
                            advertLongitude = place.getLocation().longitude;
                            locationChosen = true;
                        }

                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR && result.getData() != null) {
                        Toast.makeText(this, "Could not choose location", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        editLocation.setOnClickListener(v -> openPlacePicker());

        btnCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        btnChooseImage.setOnClickListener(v -> openImagePicker());

        btnSaveAdvert.setOnClickListener(v -> saveAdvert());
    }

    private void openPlacePicker() {

        List<Place.Field> fields = Arrays.asList(
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
        ).build(this);

        placePickerLauncher.launch(intent);
    }

    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location == null) {
                        Toast.makeText(this, "Turn on device location and try again", Toast.LENGTH_LONG).show();
                        return;
                    }

                    advertLatitude = location.getLatitude();
                    advertLongitude = location.getLongitude();
                    locationChosen = true;

                    String locationText = advertLatitude + ", " + advertLongitude;
                    editLocation.setText(locationText);

                    Toast.makeText(this, "Current location selected", Toast.LENGTH_SHORT).show();
                });
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

        if (!locationChosen) {
            Toast.makeText(this, "Please choose a location", Toast.LENGTH_SHORT).show();
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
                createdAt,
                advertLatitude,
                advertLongitude
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();

        } else {
            Toast.makeText(this, "Location permission is needed", Toast.LENGTH_SHORT).show();
        }
    }
}