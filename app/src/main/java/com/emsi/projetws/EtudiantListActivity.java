package com.emsi.projetws;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.emsi.projetws.adapter.EtudiantAdapter;
import com.emsi.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EtudiantListActivity extends AppCompatActivity {
    private RequestQueue requestQueue; // Declare at the top of the classA

    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private ArrayList<Etudiant> etudiants = new ArrayList<>();
    private String fetchUrl = "http://192.168.100.85/projet/Source Files/ws/loadEtudiant.php"; // Update if needed

    private static final String TAG = "EtudiantListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiants);

        recyclerView = findViewById(R.id.recyclerView);
        etudiants = new ArrayList<>();
        adapter = new EtudiantAdapter(etudiants, this::onItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Test with static data
        List<Etudiant> testStudents = Arrays.asList(
                new Etudiant(1, "John", "Doe", "Casablanca", "homme", null, "1990-01-01"),
                new Etudiant(2, "Jane", "Doe", "Rabat", "femme", null, "1992-05-15")
        );
        etudiants.clear();
        etudiants.addAll(testStudents);
        adapter.notifyDataSetChanged();
    }

    private void loadEtudiants() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                fetchUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Raw response: " + response);
                try {
                    Type type = new TypeToken<Collection<Etudiant>>() {}.getType();
                    Collection<Etudiant> fetchedStudents = new Gson().fromJson(response, type);

                    if (fetchedStudents == null || fetchedStudents.isEmpty()) {
                        Toast.makeText(EtudiantListActivity.this, "No students found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d(TAG, "Parsed students: " + fetchedStudents.toString());
                    etudiants.clear();
                    etudiants.addAll(fetchedStudents);
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Adapter item count: " + adapter.getItemCount());
                    Toast.makeText(EtudiantListActivity.this, "Loaded " + fetchedStudents.size() + " students", Toast.LENGTH_SHORT).show();
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "JSON parsing error", e);
                    Toast.makeText(EtudiantListActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(TAG, "General error", e);
                    Toast.makeText(EtudiantListActivity.this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(EtudiantListActivity.this, "Load error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(request);
    }

    // Handle item click events
    private void onItemClick(Etudiant etudiant) {
        Toast.makeText(this, "Clicked on: " + etudiant.getNom() + " " + etudiant.getPrenom(), Toast.LENGTH_SHORT).show();
        // You can add further actions here, such as navigating to a details screen or showing an edit dialog
    }
}