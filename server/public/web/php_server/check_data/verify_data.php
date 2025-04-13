<?PHP
/**
| * Método que verifica si si los datos introducidos en
| * son solo texto
 * 
 * @param string $text contiene nombre y apellidos.
 * @return bool devuelve true si contiene carácteres válidos sino false.
 */
function isCheckText($text)
{
    // Expresión regular para buscar caracteres no permitidos
    $pattern = "/^[a-zA-ZÁÉÍÓÚáéíóúÜü -]+$/";

    // Verificar si el texto contiene caracteres no permitidos
    if (preg_match($pattern, $text)) {
        return true;
    } else {
        // Si se encuentra algún carácter no permitido, devolver falso
        return false;
    }
}

/**
| * Método que verifica si el DNI con la letra es válido.
 * 
 * @param $dni contiene el DNI introducido por el usuario.
 * @return bool se devuelve true si es correcto sino false.
 */
function isCheckDNI($dni)
{
    $letters = array("T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", 
    "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E");
    $letter_DNI = strtoupper(substr($dni, 8));
    $num_DNI = substr($dni, 0, 8);
    $rest = $num_DNI % 23;
    if ($letter_DNI == $letters[$rest]) {
        return true;
    } else {
        return false;
    }
}

/**
| * Método que verifica si el movil es númerico e igual a longitud 9
 * 
 * @param int $cell_phone movil introducido por el usuario.
 * @return bool devuelve true si es correcto el movil sino devuelve false.
 */
function isCheckCellular($cell_phone)
{   
    if (is_numeric($cell_phone) && strlen($cell_phone) == 9) {
        return true;
    } else {
        return false;
    }
}

/**
| * Método que devuelve si el correo es válido en construcción y en Mail Exchange.
 * 
 * @param $mail correo introducido por el usuario.
 * @return bool devuelve true si es correcto y devuelve conexión MX sino false.
 */
function isCheckMail($mail)
{
    // Primero eliminamos cualquier carácter que pueda dar problemas
    $mail = filter_var($mail, FILTER_SANITIZE_EMAIL);

    // Verifica si el formato del correo es válido
    if (!filter_var($mail, FILTER_VALIDATE_EMAIL)) {
        return false;
    }

    // Verifica si el dominio del correo tiene registros MX (Mail Exchange)
    list($user, $domain) = explode('@', $mail);
    if (!checkdnsrr($domain, 'MX')) {
        return false;
    }

    return true;
}
?>