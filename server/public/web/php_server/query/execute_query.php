<?PHP
require_once __DIR__ . "/../login/verify_login.php";

/**
|* Contiene las query y la función para ejecutar la consultar y devolver el resultado
 * 
 * @version 0.1.
 * 
 * @author Carlos Abad Vidal
 * 
 * @copyright 2024 - 2025 Colexion Karbo ©
 * 
 */

/**
|* Función que ejecuta las sentencias básicas para mostrar datos al usuario
 * @param string $query Contiene la sentencia SQL a ejecutar
 * @param string|null $param Contiene los tipos de parámetros a consultar (null si no hay parámetros)
 * @param mixed|null $data Contiene el valor por el cual se quiere filtrar (null si no se necesita)
 * @return array|null Devuelve un array asociativo con los datos obtenidos o null si no hay resultados
 */
function executeSelect($query, $param = '', $data = '')
{
    $mysqli = verify_login::createConnectDB();
    // Se verifica si hay param sino hay se ejecuta directamente la sentencia
    if ($param === '') {
        $result = $mysqli->query($query);
    } else {
        // Si hay $param se ejecuta la sentencia con bind_param
        $stmt = $mysqli->prepare($query);
        if ($stmt === false) {
            // Manejo de error en caso de falla en la preparación
            error_log("Error en la preparación de la consulta: " . $mysqli->error);
            return null;
        }
        $stmt->bind_param($param, $data);
        $stmt->execute();
        $result = $stmt->get_result();
        $stmt->close(); // Cerrar declaración después de usarla
    }
    // Si hay resultados se pasan a un array_asociativo
    $rows = [];
    if ($result && $result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $rows[] = $row;
        }
    } else {
        return null;
    }

    $mysqli->close();
    return $rows;
}



/**
|* Gestiona las consultas básicas para mostrar datos al usuario
 * @param int $option Contiene el tipo de consulta que se quiere realizar
 * @param mixed|null $data Contiene el valor por el cual se quiere filtrar (null si no se necesita)
 * @return array|bool Devuelve un array asociativo con los datos obtenidos o un boolean si no hay resultados
 */
function basicQuery($option, $data = '')
{
    switch ($option) {
        case 1: {
            $query = "SELECT ID_MARCAS, NOMBRE_MARCA FROM MARCAS";
            $result = executeSelect($query);
            break;
        }
        case 2: {
            $query = "SELECT NOMBRE_MODELO FROM MODELO WHERE MARCA_MODELO_FK = ?";
            $param = "i";
            $result = executeSelect($query, $param, $data);
            break;
        }
        case 3: {
            $query = "SELECT NOMBRE FROM CATEGORIAS_PIEZAS";
            $result = executeSelect($query);
            break;
        }
        case 4: {
            $query = "SELECT PIEZAS.ID_PIEZAS, PIEZAS.DESCRIPCION, PIEZAS.PESO, PIEZAS.PRECIO, PIEZAS.REFERENCIA, 
                    PIEZAS.FECHA_YEAR, PIEZAS.IMAGENES,  VENDEDOR.NOMBRE_EMPRESA, MODELO.NOMBRE_MODELO
                    FROM PIEZAS 
                    JOIN VENDEDOR ON PIEZAS.PIEZAS_VENDEDOR_FK = VENDEDOR.ID_VENDEDOR 
                    JOIN PIEZAS_MODELOS ON PIEZAS.ID_PIEZAS = PIEZAS_MODELOS.ID_PIEZAS_FK
                    JOIN MODELO ON PIEZAS_MODELOS.ID_MODELOS_FK = MODELO.ID_MODELO
                    WHERE PIEZAS_CATEGORIAS_FK = ? AND PIEZAS.VENDIDO = FALSE";
            $param = "i";
            $result = executeSelect($query, $param, $data);
            break;
        }
        case 5: {
            $query = "SELECT PIEZAS.ID_PIEZAS, PIEZAS.DESCRIPCION, PIEZAS.PESO, PIEZAS.PRECIO, PIEZAS.REFERENCIA, 
                    PIEZAS.FECHA_YEAR, PIEZAS.IMAGENES, VENDEDOR.NOMBRE_EMPRESA, MODELO.NOMBRE_MODELO
                    FROM PIEZAS 
                    JOIN VENDEDOR ON PIEZAS.PIEZAS_VENDEDOR_FK = VENDEDOR.ID_VENDEDOR 
                    JOIN PIEZAS_MODELOS ON PIEZAS.ID_PIEZAS = PIEZAS_MODELOS.ID_PIEZAS_FK
                    JOIN MODELO ON PIEZAS_MODELOS.ID_MODELOS_FK = MODELO.ID_MODELO
                    WHERE PIEZAS.VENDIDO = FALSE AND MODELO.ID_MODELO = ?";
            $param = "i";
            $result = executeSelect($query, $param,  $data);
            break;
        }
    }

    if($result != null) {
        return $result;
    } else {
        return false;
    }
}