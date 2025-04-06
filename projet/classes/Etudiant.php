<?php
class Etudiant {
    private $id;
    private $nom;
    private $prenom;
    private $ville;
    private $sexe;
    private $photo;
    private $date_naissance;
    
    function __construct($id, $nom, $prenom, $ville, $sexe, $photo = null, $date_naissance = null) {
        $this->id = $id;
        $this->nom = $nom;
        $this->prenom = $prenom;
        $this->ville = $ville;
        $this->sexe = $sexe;
        $this->photo = $photo;
        $this->date_naissance = $date_naissance;
    }
    
    function getId() {
        return $this->id;
    }
    
    function getNom() {
        return $this->nom;
    }
    
    function getPrenom() {
        return $this->prenom;
    }
    
    function getVille() {
        return $this->ville;
    }
    
    function getSexe() {
        return $this->sexe;
    }
    
    function getPhoto() {
        return $this->photo;
    }
    
    function getDateNaissance() {
        return $this->date_naissance;
    }
    
    function setId($id) {
        $this->id = $id;
    }
    
    function setNom($nom) {
        $this->nom = $nom;
    }
    
    function setPrenom($prenom) {
        $this->prenom = $prenom;
    }
    
    function setVille($ville) {
        $this->ville = $ville;
    }
    
    function setSexe($sexe) {
        $this->sexe = $sexe;
    }
    
    function setPhoto($photo) {
        $this->photo = $photo;
    }
    
    function setDateNaissance($date_naissance) {
        $this->date_naissance = $date_naissance;
    }
    
    public function __toString() {
        return $this->nom . " " . $this->prenom;
    }
}
