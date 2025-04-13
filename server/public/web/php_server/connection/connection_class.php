<?PHP
require_once __DIR__ . "/connection.php";

/**
|* Clase que instancia y devuelve la conexión MySQL
 * 
 * @version 0.1.
 * 
 * @author Carlos Abad Vidal
 * 
 * @copyright 2024 - 2025 Colexion Karbo ©
 * 
 */
class connection
{
    private $mysqli;

    /**
     * Constructor de clase conexion que realiza la conexión con el servidor estableciendo los datos de dicha conexión
     */
    public function __construct()
    {
        try {
            $this->mysqli = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
            mysqli_set_charset($this->mysqli, "utf8");
            // Manejar errores de conexión
            if ($this->mysqli->connect_error) {
                die('Error de conexión a la base de datos: ' . $this->mysqli->connect_error);
            }
        } catch (Exception $e) {
            // Pendiente devolver error por AJAX o GET
            die("Error: Fallo al instanciar la conexión en el constructor de la clase conexion");
        }
    }

    /**
     * Función que devuelve la conexión.
     * 
     * @access public
     * 
     * @return mysqli Devuelve la conexión al servidor.
     */
    public function getInstance()
    {
        return $this->mysqli;
    }
}