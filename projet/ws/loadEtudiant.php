<?php
// Accept both GET and POST requests
if ($_SERVER["REQUEST_METHOD"] == "GET" || $_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/EtudiantService.php';
    loadAll();
}

function loadAll() {
    $es = new EtudiantService();
    header('Content-type: application/json');
    echo json_encode($es->findAllApi());
}
