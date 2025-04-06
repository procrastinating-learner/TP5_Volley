package com.emsi.projetws.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emsi.projetws.R;
import com.emsi.projetws.beans.Etudiant;

import java.util.ArrayList;
import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {

    private List<Etudiant> etudiants;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Etudiant etudiant);
    }

    public EtudiantAdapter(List<Etudiant> etudiants, OnItemClickListener listener) {
        this.etudiants = etudiants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_etudiant, parent, false);
        return new EtudiantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);
        holder.bind(etudiant, listener);
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView nomPrenomTextView, villeTextView, sexeTextView, dateNaissanceTextView;
        ImageView imageView;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            nomPrenomTextView = itemView.findViewById(R.id.tvNomPrenom);
            villeTextView = itemView.findViewById(R.id.tvVille);
            sexeTextView = itemView.findViewById(R.id.tvSexe);
            dateNaissanceTextView = itemView.findViewById(R.id.tvDateNaissance);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(final Etudiant etudiant, final OnItemClickListener listener) {
            nomPrenomTextView.setText(etudiant.getNom() + " " + etudiant.getPrenom());
            villeTextView.setText("Ville: " + (etudiant.getVille() != null ? etudiant.getVille() : "Non spécifiée"));
            sexeTextView.setText("Sexe: " + (etudiant.getSexe() != null ? etudiant.getSexe() : "Non spécifié"));

            if (etudiant.getDateNaissance() != null && !etudiant.getDateNaissance().isEmpty()) {
                dateNaissanceTextView.setText("Date de naissance: " + etudiant.getDateNaissance());
            } else {
                dateNaissanceTextView.setText("Date de naissance: Non spécifiée");
            }

            if (etudiant.getPhoto() != null && !etudiant.getPhoto().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(etudiant.getPhoto(), Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedBitmap);
                } catch (Exception e) {
                    imageView.setImageResource(R.drawable.ic_person_placeholder); // Default placeholder
                }
            } else {
                imageView.setImageResource(R.drawable.ic_person_placeholder); // Default placeholder
            }

            itemView.setOnClickListener(v -> listener.onItemClick(etudiant));
        }
    }
}