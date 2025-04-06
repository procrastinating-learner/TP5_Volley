package com.emsi.projetws;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.projetws.adapter.EtudiantAdapter;
import com.emsi.projetws.beans.Etudiant;

import java.util.HashMap;
import java.util.Map;

public class EtudiantPopup {
    private static final String UPDATE_URL = "http://192.168.100.85/projet/Source Files/ws/updateEtudiant.php";
    private static final String DELETE_URL = "http://192.168.100.85/projet/Source Files/ws/deleteEtudiant.php";

    public static void show(Context context, Etudiant etudiant, EtudiantAdapter adapter, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.etudiant_popup, null);

        EditText nom = view.findViewById(R.id.editNom);
        EditText prenom = view.findViewById(R.id.editPrenom);
        EditText ville = view.findViewById(R.id.editVille);
        EditText sexe = view.findViewById(R.id.editSexe);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        // Pre-fill with current values
        nom.setText(etudiant.getNom());
        prenom.setText(etudiant.getPrenom());
        ville.setText(etudiant.getVille());
        sexe.setText(etudiant.getSexe());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Update handler
        btnUpdate.setOnClickListener(v -> {
            // Logging the parameters to debug
            Log.d("UpdateEtudiant", "ID: " + etudiant.getId());
            Log.d("UpdateEtudiant", "Nom: " + nom.getText().toString());
            Log.d("UpdateEtudiant", "Prenom: " + prenom.getText().toString());
            Log.d("UpdateEtudiant", "Ville: " + ville.getText().toString());
            Log.d("UpdateEtudiant", "Sexe: " + sexe.getText().toString());

            StringRequest updateRequest = new StringRequest(Request.Method.POST, UPDATE_URL,
                    response -> {
                        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                        Log.e("UpdateResponse", response);
                        //   updateEtudiantInList(etudiant);
                        dialog.dismiss();
                    },
                    error -> {
                        Toast.makeText(context, "Update failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("UpdateError", error.toString());  // Log error for debugging
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", String.valueOf(etudiant.getId()));
                    params.put("nom", nom.getText().toString());
                    params.put("prenom", prenom.getText().toString());
                    params.put("ville", ville.getText().toString());
                    params.put("sexe", sexe.getText().toString());
                    return params;
                }
            };
            Volley.newRequestQueue(context).add(updateRequest);
            etudiant.setNom(nom.getText().toString());
            etudiant.setPrenom(prenom.getText().toString());
            etudiant.setVille(ville.getText().toString());
            etudiant.setSexe(sexe.getText().toString());
            adapter.notifyItemChanged(position);
        });




        // Delete handler
        btnDelete.setOnClickListener(v -> {
            Log.d("DeleteEtudiant", "ID to delete: " + etudiant.getId());

            StringRequest deleteRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                    response -> {
                        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        // removeEtudiantFromList(etudiant);
                        dialog.dismiss();
                    },
                    error -> {
                        Toast.makeText(context, "Delete failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("DeleteError", error.toString());  // Log error for debugging
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", String.valueOf(etudiant.getId()));
                    return params;
                }
            };
            Volley.newRequestQueue(context).add(deleteRequest);
        });

    }
}
