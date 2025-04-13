<?PHP
require_once __DIR__ . "/../connection/connection_class.php";
/**
|* Clase que gestiona la autenticación del usuario y crea la conexione a la base de datos
|* a través de la clase connection.
 *
 * @version 0.1.
 * 
 * @author Carlos Abad Vidal
 * 
 * @copyright 2024 - 2025 Colexion Karbo ©
 * 
 * @method void __construct() Constructor privado de la clase verify_login.
 * @method void saveSession($user, $pass, $privilege_lvl) Establece las variables de sesión del usuario.
 * @method bool checkCredentials($user, $pass) Verifica las credenciales del usuario.
 * @method void checkPrivileges() Determina la redirección según los privilegios del usuario.
 * @method mysqli|null createConnectDB() Crea la conexión a la base de datos para consultas básicas.
 */
class verify_login
{

    private function __construct()
    {
    }



    /**
    |* Función que establece como variables de sesión el nombre, la contraseña 
    |* y el nivel de privilegio del que dispone el usuario.
     * 
     * @param string $user nombre del usuario verificado.
     * @param string $pass contraseña encriptada del usuario.
     * @param string $privilege_lvl nivel de creedencial del usuario en la base datos.
     */
    private static function saveSession($user, $pass, $privilege_lvl, $name, $last_name, $id)
    {
        // Iniciar la sesión si no está iniciada
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }

        // Almacenar los valores en la sesión
        $_SESSION['usuario'] = $user;
        $_SESSION['clave'] = $pass;
        $_SESSION['nivel_privilegios'] = $privilege_lvl;
        $_SESSION['nombre'] = $name;
        $_SESSION['apellido'] = $last_name;
        $_SESSION['id'] = $id;
    }

    /**
    | * Función que verifica si las credenciales ingresadas por el usuario son correctas.
     * 
     * @param string $user usuario para el inicio de sesión del usuario.
     * @param string $pass contraseña para el inicio de sesión del usuario.
     * @return bool true si las credenciales son correctas, en caso contrario false.
     */
    public static function checkCredentials($user, $pass)
    {
        //require_once ('C:\config\config.php');
        //$mysqli = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

        global $privilege_lvl;

        $mysqli = verify_login::createConnectDB();

        // Verificar el nivel de privilegio del usuario pendiente de concretar forma de encriptación de la contraseña
        $stmt = $mysqli->prepare("SELECT ID_CLIENTE, NOMBRE, APELLIDO_A, 'CLIENTE' AS ORIGEN 
                                  FROM CLIENTE 
                                  WHERE MAIL = ? AND CONTRASENA = SHA2(?, 256)
                                  UNION
                                  SELECT ID_EMPLEADOS, NOMBRE, APELLIDO_A, 'EMPLEADO' AS ORIGEN 
                                  FROM EMPLEADOS 
                                  WHERE MAIL = ? AND CONTRASENA = SHA2(?, 256)");
        $stmt->bind_param("ssss", $user, $pass, $user, $pass);
        $stmt->execute();
        $result = $stmt->get_result();
        // Si hay resultado se obtiene la tabla a la que pertenece el usuario
        if ($result->num_rows == 1) {
            $row = $result->fetch_assoc();
            var_dump($row);
            $privilege_lvl = $row['ORIGEN'];
            $id = $row['ID_CLIENTE'];
            $name = $row['NOMBRE'];
            $last_name = $row['APELLIDO_A'];
            verify_login::saveSession($user, $pass, $privilege_lvl, $name, $last_name, $id);
            $stmt->close();
            $mysqli->close();
            return true;
        } else {
            return false;
        }
    }

    /**
    |* Función que determina la ruta del usuario en base a sus privilegios.
     * 
     */
    public static function checkPrivileges()
    {
        $privilege_lvl = $_SESSION['nivel_privilegios'];
        $name = $_SESSION['nombre'];
        $last_name = $_SESSION['apellido'];
        $id = $_SESSION['id'];
        
        switch ($privilege_lvl) {
            case 'CLIENTE':
                // Usuario
                return $id . ';;' . $name . ' ' . $last_name;
                //header("Location: ../../php/clientes/clientePrincipal.php");
                //exit();
            case 'EMPLEADO':
                // Empleado
                return $id . ';;' . $name . ' ' . $last_name;;
                //header("Location: ../../php/empleados/administrador.php");
                //exit();
            default:
                //header("Location: ../../php/login.php?error=privilegios");
                exit();
        }
    }


    /**
    |* Función global para crear la conexión a la base de datos.
     * 
     * @return mysqli|null Devuelve la conexión MySQL si es exitosa, o null si falla.
     */
    public static function createConnectDB()
    {
        $connection = new Connection();

        // Verificar si hay un error en la conexión
        if ($connection->getInstance()->connect_error) {
            return null;
        }

        return $connection->getInstance();
    }
}