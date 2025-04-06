<?php
include_once RACINE . '/classes/Etudiant.php';
include_once RACINE . '/connexion/Connexion.php';
include_once RACINE . '/dao/IDao.php';

class EtudiantService implements IDao {
    private $connexion;
    
    function __construct() {
        $this->connexion = new Connexion();
    }
    
    public function create($o) {
        // Use proper parameter binding to prevent SQL injection
        $query = "INSERT INTO Etudiant (`id`, `nom`, `prenom`, `ville`, `sexe`, `photo`, `date_naissance`)
                  VALUES (NULL, ?, ?, ?, ?, ?, ?)";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute(array(
            $o->getNom(),
            $o->getPrenom(),
            $o->getVille(),
            $o->getSexe(),
            $o->getPhoto(),
            $o->getDateNaissance()
        )) or die('Erreur SQL: ' . implode(' ', $req->errorInfo()));
    }
    
    public function delete($id) {
        // Updated to accept an ID directly instead of an object
        $query = "DELETE FROM Etudiant WHERE id = ?";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute(array($id)) or die('Erreur SQL: ' . implode(' ', $req->errorInfo()));
    }
    
    public function findAll() {
        $etds = array();
        $query = "SELECT * FROM Etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        
        while ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $etds[] = new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe, $e->photo, $e->date_naissance);
        }
        return $etds;
    }
    
    public function findById($id) {
        // Use proper parameter binding
        $query = "SELECT * FROM Etudiant WHERE id = ?";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute(array($id));
        
        $etd = null;
        if ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $etd = new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe, $e->photo, $e->date_naissance);
        }
        return $etd;
    }
    
    public function update($o) {
        // Use proper parameter binding
        $query = "UPDATE `etudiant`
                  SET `nom` = ?,
                      `prenom` = ?,
                      `ville` = ?,
                      `sexe` = ?,
                      `photo` = ?,
                      `date_naissance` = ?
                  WHERE `etudiant`.`id` = ?";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute(array(
            $o->getNom(),
            $o->getPrenom(),
            $o->getVille(),
            $o->getSexe(),
            $o->getPhoto(),
            $o->getDateNaissance(),
            $o->getId()
        )) or die('Erreur SQL: ' . implode(' ', $req->errorInfo()));
    }
    
    public function findAllApi() {
        $query = "SELECT * FROM Etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
