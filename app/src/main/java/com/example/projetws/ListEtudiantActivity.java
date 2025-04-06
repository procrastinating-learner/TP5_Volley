package com.example.projetws;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.adapter.EtudiantAdapter;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListEtudiantActivity extends AppCompatActivity implements EtudiantAdapter.OnItemClickListener {

    private static final String TAG = "ListEtudiantsActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int PERMISSIONS_REQUEST_STORAGE = 101;

    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiantList;
    private Button addButton;
    private ImageView currentPhotoImageView;

    private RequestQueue requestQueue;
    // Update these URLs
    private String fetchUrl = "http://192.168.100.85/projet/ws/loadEtudiant.php";
    private String updateUrl = "http://192.168.100.85/projet/ws/updateEtudiant.php";
    private String deleteUrl = "http://192.168.100.85/projet/ws/deleteEtudiant.php";




    // Variables for photo handling
    private String currentPhotoPath;
    private String encodedImage = null;
    private String selectedDate = null;
    private Etudiant currentEditingEtudiant = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiant);

        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);

        etudiantList = new ArrayList<>();
        adapter = new EtudiantAdapter(etudiantList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ListEtudiantActivity.this, AddEtudiant.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting AddEtudiant activity", e);
                    Toast.makeText(ListEtudiantActivity.this,
                            "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });



        // Load students when activity is created
        loadEtudiants();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload students when returning to this activity
        loadEtudiants();
    }

    private void loadEtudiants() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                fetchUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                Collection<Etudiant> etudiants = new Gson().fromJson(response, type);

                etudiantList.clear();
                etudiantList.addAll(etudiants);
                adapter.notifyDataSetChanged();
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

                Toast.makeText(ListEtudiantActivity.this,
                        "Erreur lors du chargement: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    @Override
    public void onItemClick(final Etudiant etudiant) {
        // Show options dialog (Edit or Delete)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options")
                .setItems(new CharSequence[]{"Modifier", "Supprimer"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Modify
                                showEditDialog(etudiant);
                                break;
                            case 1: // Delete
                                showDeleteConfirmation(etudiant);
                                break;
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showEditDialog(final Etudiant etudiant) {
        currentEditingEtudiant = etudiant;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_etudiant, null);
        builder.setView(dialogView);

        // Initialize dialog views
        final ImageView photoImageView = dialogView.findViewById(R.id.editPhotoImageView);
        currentPhotoImageView = photoImageView;
        final EditText nomEditText = dialogView.findViewById(R.id.editNomEditText);
        final EditText prenomEditText = dialogView.findViewById(R.id.editPrenomEditText);
        final Spinner villeSpinner = dialogView.findViewById(R.id.editVilleSpinner);
        final RadioButton hommeRadioButton = dialogView.findViewById(R.id.editHommeRadioButton);
        final RadioButton femmeRadioButton = dialogView.findViewById(R.id.editFemmeRadioButton);

        // New fields for photo and date of birth

        final Button takePhotoButton = dialogView.findViewById(R.id.editTakePhotoButton);
        final TextView dateNaissanceTextView = dialogView.findViewById(R.id.editDateNaissanceTextView);
        final Button selectDateButton = dialogView.findViewById(R.id.editSelectDateButton);

        // Set current values
        nomEditText.setText(etudiant.getNom());
        prenomEditText.setText(etudiant.getPrenom());

        // Set current date of birth if available
        if (etudiant.getDate_naissance() != null && !etudiant.getDate_naissance().isEmpty()) {
            // Format for display
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputFormat.parse(etudiant.getDate_naissance());
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                dateNaissanceTextView.setText(displayFormat.format(date));
            } catch (Exception e) {
                dateNaissanceTextView.setText(etudiant.getDate_naissance());
            }
            selectedDate = etudiant.getDate_naissance();
        } else {
            dateNaissanceTextView.setText("Non spécifiée");
            selectedDate = null;
        }

        // Set current photo if available
        if (etudiant.getPhoto() != null && !etudiant.getPhoto().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(etudiant.getPhoto(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                photoImageView.setImageBitmap(decodedBitmap);
                encodedImage = etudiant.getPhoto();
            } catch (Exception e) {
                photoImageView.setImageResource(R.drawable.ic_person_placeholder);
                encodedImage = null;
            }
        } else {
            photoImageView.setImageResource(R.drawable.ic_person_placeholder);
            encodedImage = null;
        }

        // Setup ville spinner
        String[] villes = new String[]{
                "Casablanca", "Rabat", "Marrakech", "Fès", "Tanger", "Agadir",
                "Meknès", "Oujda", "Kénitra", "Tétouan", "Safi", "El Jadida",
                "Mohammedia", "Béni Mellal", "Nador", "Taza", "Settat"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, villes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villeSpinner.setAdapter(adapter);

        // Set current ville
        for (int i = 0; i < villes.length; i++) {
            if (villes[i].equals(etudiant.getVille())) {
                villeSpinner.setSelection(i);
                break;
            }
        }

        // Set current sexe
        if ("homme".equals(etudiant.getSexe())) {
            hommeRadioButton.setChecked(true);
        } else {
            femmeRadioButton.setChecked(true);
        }

        // Setup date picker button
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        ListEtudiantActivity.this,
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
        });

        // Setup photo buttons
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request camera permission and take photo
                if (ContextCompat.checkSelfPermission(ListEtudiantActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListEtudiantActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });


        /*
        pickPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request storage permission and pick photo
                if (ContextCompat.checkSelfPermission(ListEtudiantsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListEtudiantsActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_STORAGE);
                } else {
                    dispatchPickPictureIntent();
                }
            }
        });*/

        // Setup dialog buttons
        builder.setTitle("Modifier l'étudiant")
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate inputs
                        String nom = nomEditText.getText().toString().trim();
                        String prenom = prenomEditText.getText().toString().trim();

                        if (nom.isEmpty() || prenom.isEmpty()) {
                            Toast.makeText(ListEtudiantActivity.this,
                                    "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get selected sexe
                        String sexe = hommeRadioButton.isChecked() ? "homme" : "femme";

                        // Update student
                        updateEtudiant(etudiant.getId(), nom, prenom,
                                villeSpinner.getSelectedItem().toString(), sexe,
                                encodedImage, selectedDate);
                    }
                })
                .setNegativeButton("Annuler", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmation(final Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Êtes-vous sûr de vouloir supprimer " +
                        etudiant.getNom() + " " + etudiant.getPrenom() + " ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEtudiant(etudiant.getId());
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void updateEtudiant(final int id, final String nom, final String prenom,
                                final String ville, final String sexe,
                                final String photo, final String dateNaissance) {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                updateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update response: " + response);

                try {
                    // Parse response
                    Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                    Collection<Etudiant> etudiants = new Gson().fromJson(response, type);

                    // Update list
                    etudiantList.clear();
                    etudiantList.addAll(etudiants);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(ListEtudiantActivity.this,
                            "Étudiant modifié avec succès", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing update response", e);
                    Toast.makeText(ListEtudiantActivity.this,
                            "Erreur lors du parsing: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
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

                Log.e(TAG, "Error updating student: " + errorMessage, error);

                Toast.makeText(ListEtudiantActivity.this,
                        "Erreur lors de la modification: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("ville", ville);
                params.put("sexe", sexe);

                // Add photo if available
                if (photo != null) {
                    params.put("photo", photo);
                }

                // Add date of birth if available
                if (dateNaissance != null) {
                    params.put("date_naissance", dateNaissance);
                }

                return params;
            }
        };

        requestQueue.add(request);
    }

    private void deleteEtudiant(final int id) {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                deleteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete response: " + response);

                try {
                    // Parse response
                    Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                    Collection<Etudiant> etudiants = new Gson().fromJson(response, type);

                    // Update list
                    etudiantList.clear();
                    etudiantList.addAll(etudiants);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(ListEtudiantActivity.this,
                            "Étudiant supprimé avec succès", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing delete response", e);
                    Toast.makeText(ListEtudiantActivity.this,
                            "Erreur lors du parsing: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
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

                Log.e(TAG, "Error deleting student: " + errorMessage, error);

                Toast.makeText(ListEtudiantActivity.this,
                        "Erreur lors de la suppression: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        requestQueue.add(request);
    }

    // Methods for handling photo capture and selection
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

    private void dispatchPickPictureIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE);
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
        // Remove the PERMISSIONS_REQUEST_STORAGE case
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle camera photo
                processCapturedPhoto();
            }
            // Remove the REQUEST_PICK_IMAGE case
        }
    }

    private void processCapturedPhoto() {
        try {
            // Get the dimensions of the View
            int targetW = 500;
            int targetH = 500;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

            // Resize bitmap if it's too large
            bitmap = resizeBitmap(bitmap, 1024);

            // Use the stored reference to update the ImageView
            if (currentPhotoImageView != null) {
                currentPhotoImageView.setImageBitmap(bitmap);
                encodedImage = encodeImage(bitmap);
            }

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
}
