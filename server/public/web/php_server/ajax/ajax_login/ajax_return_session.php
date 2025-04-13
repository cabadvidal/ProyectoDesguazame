<?php
session_start();

header('Content-Type: application/json');

echo json_encode([
    'user_id' => $_SESSION['id'] ?? null,
    'username' => $_SESSION['nombre'] ?? null,
    'last_name' => $_SESSION['apellido'] ?? null,
    'privilege_lvl' => $_SESSION['nivel_privilegios'] ?? null
]);
?>