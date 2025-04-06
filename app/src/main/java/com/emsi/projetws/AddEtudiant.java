package com.emsi.projetws;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.emsi.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEtudiant extends AppCompatActivity {

    private static final String TAG = "AddEtudiant";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 100;
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    private Button btnAfficher;
    private ImageView photoImageView;
    private Button takePhotoButton;
    private TextView dateNaissanceTextView;
    private Button selectDateButton;
    private String currentPhotoPath;
    private String encodedImage = null;
    private String selectedDate = null;
    RequestQueue requestQueue;
    String insertUrl = "http://192.168.100.85/projet/Source Files/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        try {

            nom = (EditText) findViewById(R.id.nomEditText);
            prenom = (EditText) findViewById(R.id.prenomEditText);
            ville = (Spinner) findViewById(R.id.villeSpinner);
            add = (Button) findViewById(R.id.ajouterButton);
            m = (RadioButton) findViewById(R.id.hommeRadioButton);
            f = (RadioButton) findViewById(R.id.femmeRadioButton);
            btnAfficher = findViewById(R.id.list);
            photoImageView = findViewById(R.id.photoImageView);
            takePhotoButton = findViewById(R.id.takePhotoButton);
            dateNaissanceTextView = findViewById(R.id.dateNaissanceTextView);
            selectDateButton = findViewById(R.id.selectDateButton);

            // Setup click listeners
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateInputs()) {
                        addEtudiant();
                    }
                }
            });
            btnAfficher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddEtudiant.this, EtudiantListActivity.class);
                    startActivity(intent);
                }
            });

            takePhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(AddEtudiant.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddEtudiant.this,
                                new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                    } else {
                        dispatchTakePictureIntent();
                    }
                }
            });

            selectDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog();
                }
            });

            // Setup ville spinner
            setupVilleSpinner();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupVilleSpinner() {
        // List of Moroccan cities
        String[] villes = new String[]{
                "-- Choisir une ville --",
                "Agadir", "Ait Melloul", "Akhenfir", "Al Hoceïma", "Azilal",
                "Azrou", "Béni Mellal", "Berrechid", "Boujdour", "Casablanca",
                "Chefchaouen", "Dakhla", "Demnate", "El Jadida", "Errachidia",
                "Essaouira", "Fquih Ben Salah", "Fès", "Guelmim", "Ifrane",
                "Jerada", "Khémisset", "Khouribga", "Ksar El Kebir", "Kénitra",
                "Larache", "Laâyoune", "Marrakech", "Meknès", "Midelt",
                "Mohammedia", "Nador", "Ouarzazate", "Ouezzane", "Oujda",
                "Rabat", "Safi", "Settat", "Sidi Bennour", "Sidi Kacem",
                "Sidi Slimane", "Skhirat", "Smara", "Tanger", "Tan-Tan",
                "Taourirt", "Taroudant", "Taza", "Temara", "Tétouan",
                "Tiflet", "Tinghir", "Youssoufia", "Zagora"
        };




        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, villes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ville.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        // Show date picker dialog
        Calendar calendar = Calendar.getInstance();

        // If we have a date already, parse it
        if (selectedDate != null && !selectedDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(selectedDate);
                calendar.setTime(date);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date", e);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddEtudiant.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format for MySQL
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectedDate = sdf.format(calendar.getTime());

                        // Format for display
                        SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        dateNaissanceTextView.setText(displaySdf.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set max date to today (no future dates)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.emsi.projetws.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission de caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle camera photo
                processCapturedPhoto();
            }
        }
    }

    private void processCapturedPhoto() {
        try {
            int targetW = 500;
            int targetH = 500;

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

            if (bitmap == null) {
                Toast.makeText(this, "Impossible de charger l'image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Bitmap is null! Vérifie le chemin ou le fichier.");
                return;
            }

            bitmap = resizeBitmap(bitmap, 1024);
            photoImageView.setImageBitmap(bitmap);
            encodedImage = encodeImage(bitmap);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors du traitement de l'image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error processing captured photo", e);
        }
    }


    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private boolean validateInputs() {
        String nomText = nom.getText().toString().trim();
        String prenomText = prenom.getText().toString().trim();

        if (nomText.isEmpty()) {
            nom.setError("Veuillez entrer un nom");
            return false;
        }

        if (prenomText.isEmpty()) {
            prenom.setError("Veuillez entrer un prénom");
            return false;
        }

        if (!m.isChecked() && !f.isChecked()) {
            Toast.makeText(this, "Veuillez sélectionner un sexe", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addEtudiant() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {
                    Type type = new TypeToken<Collection<Etudiant>>() {
                    }.getType();
                    Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                    for (Etudiant e : etudiants) {
                        Log.d(TAG, e.toString());
                    }

                    // Clear form after successful submission
                    nom.setText("");
                    prenom.setText("");
                    m.setChecked(false);
                    f.setChecked(false);
                    ville.setSelection(0);
                    photoImageView.setImageResource(R.drawable.ic_person_placeholder);
                    encodedImage = null;
                    dateNaissanceTextView.setText("Sélectionner une date");
                    selectedDate = null;

                    Toast.makeText(AddEtudiant.this,
                            "Étudiant ajouté avec succès", Toast.LENGTH_SHORT).show();

                    // Finish this activity to return to the list
                    //finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing response", e);
                    Toast.makeText(AddEtudiant.this,
                            "Erreur lors du parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Unknown error";
                if (error.networkResponse != null) {
                    errorMessage = "Status code: " + error.networkResponse.statusCode;
                } else if (error.getMessage() != null) {
                    errorMessage = error.getMessage();
                }

                Log.e(TAG, "Error adding student: " + errorMessage, error);
                Toast.makeText(AddEtudiant.this,
                        "Erreur lors de l'ajout: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String sexe = "";
                if (m.isChecked())
                    sexe = "homme";
                else
                    sexe = "femme";

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("nom", nom.getText().toString());
                params.put("prenom", prenom.getText().toString());
                params.put("ville", ville.getSelectedItem().toString());
                params.put("sexe", sexe);

                // Add photo if available
                if (encodedImage != null) {
                    params.put("photo", encodedImage);
                }

                // Add date of birth if available
                if (selectedDate != null) {
                    params.put("date_naissance", selectedDate);
                }

                return params;
            }
        };
        requestQueue.add(request);
    }

}