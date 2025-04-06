<?php
if($_SERVER["REQUEST_METHOD"] == "POST"){
    // Include the racine.php file to define RACINE constant
    include_once '../racine.php';
    
    // Include the EtudiantService class
    include_once RACINE . '/service/EtudiantService.php';
    
    // Call the update function
    update();
}

function update(){
    // Check if all required fields are present
    if(!isset($_POST['id']) || !isset($_POST['nom']) || !isset($_POST['prenom']) ||
       !isset($_POST['ville']) || !isset($_POST['sexe'])) {
        
        header('Content-Type: application/json');
        echo json_encode([
            'success' => false,
            'message' => 'Missing required fields. Please provide id, nom, prenom, ville, and sexe.',
            'received' => $_POST
        ]);
        return;
    }
    
    // Extract POST data
    $id = $_POST['id'];
    $nom = $_POST['nom'];
    $prenom = $_POST['prenom'];
    $ville = $_POST['ville'];
    $sexe = $_POST['sexe'];
    $photo = isset($_POST['photo']) ? $_POST['photo'] : null;
    $date_naissance = isset($_POST['date_naissance']) ? $_POST['date_naissance'] : null;
    
    try {
        // Create student service
        $es = new EtudiantService();
        
        // Update student
        $es->update(new Etudiant($id, $nom, $prenom, $ville, $sexe, $photo, $date_naissance));
        
        // Return all students as JSON
        header('Content-type: application/json');
        echo json_encode($es->findAllApi());
    } catch (Exception $e) {
        // Return error if something goes wrong
        header('Content-Type: application/json');
        echo json_encode([
            'success' => false,
            'message' => 'Error: ' . $e->getMessage()
        ]);
    }
}
