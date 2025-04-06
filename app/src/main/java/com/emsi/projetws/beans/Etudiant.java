package com.emsi.projetws.beans;

public class Etudiant {
    private int id;
    private String nom;
    private String prenom;
    private String ville;
    private String sexe;
    private String photo; // Base64 encoded string for now (optional)
    private String date_naissance;

    // Default constructor
    public Etudiant() {}

    // Parameterized constructor
    public Etudiant(int id, String nom, String prenom, String ville, String sexe, String photo, String date_naissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.ville = ville;
        this.sexe = sexe;
        this.photo = photo;
        this.date_naissance = date_naissance;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDateNaissance() {
        return date_naissance;
    }

    public void setDateNaissance(String date_naissance) {
        this.date_naissance = date_naissance;
    }

    @Override
    public String toString() {
        return "Etudiant{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", ville='" + ville + '\'' +
                ", sexe='" + sexe + '\'' +
                ", date_naissance='" + date_naissance + '\'' +
                '}';
    }
}