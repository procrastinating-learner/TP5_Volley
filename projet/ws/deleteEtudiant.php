<?php
if($_SERVER["REQUEST_METHOD"] == "POST"){
    // Include the racine.php file to define RACINE constant
    include_once '../racine.php';
    
    // Include the EtudiantService class
    include_once RACINE . '/service/EtudiantService.php';
    
    // Call the delete function
    delete();
}

function delete(){
    // Check if id is provided
    if(!isset($_POST['id'])) {
        header('Content-Type: application/json');
        echo json_encode([
            'success' => false,
            'message' => 'Missing required field: id',
            'received' => $_POST
        ]);
        return;
    }
    
    // Extract POST data
    $id = $_POST['id'];
    
    try {
        // Create student service
        $es = new EtudiantService();
        
        // Delete student
        $es->delete($id);
        
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
