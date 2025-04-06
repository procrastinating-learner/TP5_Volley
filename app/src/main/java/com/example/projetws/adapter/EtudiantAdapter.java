package com.example.projetws.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetws.AddEtudiant;
import com.example.projetws.R;
import com.example.projetws.beans.Etudiant;
import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {

    private List<Etudiant> etudiantList;
    private OnItemClickListener listener;
    private int lastPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Etudiant etudiant);
    }

    public EtudiantAdapter(List<Etudiant> etudiantList, OnItemClickListener listener) {
        this.etudiantList = etudiantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_etudiant, parent, false);
        return new EtudiantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiantList.get(position);
        holder.bind(etudiant, listener);

        // Apply animation for new items
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return etudiantList.size();
    }

    // Reset animation when data changes
    @Override
    public void onViewDetachedFromWindow(@NonNull EtudiantViewHolder holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }

    static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        private TextView nomPrenomTextView;
        private TextView villeTextView;
        private TextView sexeTextView;
        private TextView dateNaissanceTextView;
        private ImageView photoImageView;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            nomPrenomTextView = itemView.findViewById(R.id.nomPrenomTextView);
            villeTextView = itemView.findViewById(R.id.villeTextView);
            sexeTextView = itemView.findViewById(R.id.sexeTextView);
            dateNaissanceTextView = itemView.findViewById(R.id.dateNaissanceTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }

        public void bind(final Etudiant etudiant, final OnItemClickListener listener) {
            nomPrenomTextView.setText(etudiant.getNom() + " " + etudiant.getPrenom());
            villeTextView.setText(etudiant.getVille());
            sexeTextView.setText(etudiant.getSexe());

            // Set date of birth if available
            if (etudiant.getDate_naissance() != null && !etudiant.getDate_naissance().isEmpty()) {
                dateNaissanceTextView.setText(etudiant.getDate_naissance());
            } else {
                dateNaissanceTextView.setText("Non spécifiée");
            }

            // Set photo if available
            if (etudiant.getPhoto() != null && !etudiant.getPhoto().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(etudiant.getPhoto(), Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    photoImageView.setImageBitmap(decodedBitmap);
                } catch (Exception e) {
                    // If there's an error decoding the image, use the placeholder
                    photoImageView.setImageResource(R.drawable.ic_person_placeholder);
                }
            } else {
                photoImageView.setImageResource(R.drawable.ic_person_placeholder);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(etudiant);
                }
            });
        }
    }
}
