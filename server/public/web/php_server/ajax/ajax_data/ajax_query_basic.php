<?PHP
require_once __DIR__ . "/../query/execute_query.php";

/**
|* Llamada por ajax para realizar las consultas básicas
 * 
 * @version 0.1.
 * 
 * @author Carlos Abad Vidal
 * 
 * @copyright 2024 - 2025 Colexion Karbo ©
 * 
 */

/**
 * 
 * TESTING
 * 
 */
/*

$_POST['query'] = 'marcas';
$_POST['query'] = 'categoria';
$_POST['query'] = 'piezas_categoria';
$_POST['query'] = 'piezas_modelo';
$_POST['data'] = '1';
*/

// Se verifica que se envien datos por query sino se devuelve false
if (isset($_POST['query'])) {
    $option = $_POST['query'];
    // Obtiene datos enviados en data o lo establece en null
    $data = isset($_POST['data']) ? $_POST['data'] : null;
    // Establece en null $result
    $result = null;

    // Switch que selecciona el tipo de consulta que se quiere realizar
    switch ($option) {
        case 'marcas': {
            $result = basicQuery(1);
            break;
        }
        case 'categoria': {
            $result = basicQuery(3);
            break;
        }
        case 'modelo': {
            // Verifica que se pasa el párametro de consulta en $data sino se devuelve false
            if ($data != null) {
                $result = basicQuery(2, $data);
            } else {
                echo json_encode('false');
            }
            break;
        }
        case 'piezas_categoria': {
            // Verifica que se pasa el párametro de consulta en $data sino se devuelve false
            if ($data != null) {
                $result = basicQuery(4, $data);
            } else {
                echo json_encode('false');
            }
            break;
        }
        case 'piezas_modelo': {
            // Verifica que se pasa el párametro de consulta en $data sino se devuelve false
            if ($data != null) {
                $result = basicQuery(5, $data);
            } else {
                echo json_encode('false');
            }
            break;
        }
    }
    // Devuelve los datos si la consulta es exitosa como un json sino false
    if($result != null) {
        echo json_encode($result);
        exit();
    } else {
        echo json_encode('false');       
        exit();
    }
    

} else {
    echo json_encode('false');
    exit();
}


