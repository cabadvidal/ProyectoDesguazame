<?PHP
require_once __DIR__ . "/verify_login.php";
require_once __DIR__ . "/../check_data/verify_data.php";

/**
|* Realiza la gestión para verificar creedenciales de cliente o administrador
 * 
 * @version 0.1.
 * 
 * @author Carlos Abad Vidal
 * 
 * @copyright 2024 - 2025 Colexion Karbo ©
 * 
 */


 $datos = [
    ["user" => 'carlos.sanchez@example.com', "pass" => 'contrasena1'],
    ["user" => 'lucia.perez@recambiosmotor.com', "pass" => 'empleado2']
];

for ($i = 0; $i < count($datos); $i++) {
    // Cerrar sesión anterior y reiniciar sesión
   

    $_POST['user'] = $datos[$i]['user'];
    $_POST['password'] = $datos[$i]['pass'];

if (isset($_POST['user']) && isset($_POST['password'])) {
    $user = $_POST['user'];
    $pass = $_POST['password'];
    if (isCheckMail($user)) {
        if (verify_login::checkCredentials($user, $pass)) {
            echo json_encode(verify_login::checkPrivileges());
        } else {
            echo json_encode("Contraseña incorrecta.");
            exit();
        }
    } else {
        echo json_encode("Usuario incorrecto.");
        exit();
    }
}

$privilege_lvl = $_SESSION['nivel_privilegios'];

print_r($privilege_lvl);

// Cerrar sesión actual para la siguiente iteración
session_write_close();

}