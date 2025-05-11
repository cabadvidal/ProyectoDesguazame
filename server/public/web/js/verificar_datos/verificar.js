/**
 * Lee el objecto JSON con los datos y lo lanza al switch para que se verifique en la función correspondiente.
 * @param {Object} datos Contiene un objecto JSON con la clave valor a verificar
 * @param {string} tabla Contiene el nombre de la tabla que se verifica.
 * @returns {Object} Devuelve `{ isCorrecto: true }` si todo es válido o `{ isCorrecto: false, key: "clave_erronea" }`
 */
function verificarDatos(datos, tabla) {
    for (const key of Object.keys(datos)) {
        let isCorrecto = switchDatos(key, datos[key], tabla);
        if (!isCorrecto) {
            return { isCorrecto: false, key: key };
        }
    }
    return { isCorrecto: true };
}

/**
 * Lanza el dato a la función para verificar su valor.
 * @param {string} tipo Indica que tipo de dato hay q verificar.
 * @param {string} dato Contiene el valor del dato a verificar.
 * @param {string} tabla Contiene el nombre de la tabla que se verifica.
 * @returns Devuelve el valor de la función si no hay coincidencia devuelve verdadero.
 */
function switchDatos(tipo, dato, tabla) {
    switch (tipo) {
        case 'NOMBRE': case 'APELLIDO_A': case 'APELLIDO_B': case 'MUNICIPIO': case 'CIUDAD':
            return verificarString(dato);
        case 'TELEFONO':
            return verificarTelefono(dato);
        case 'MOVIL':
            return verificarMovil(dato);
        case 'TOTAL_VACACIONES':
            return verificarVacaciones(dato);
        case 'NUMERO_CUENTA':
            return verificarNumeroCuenta(dato);
        case 'NUMERO_SS':
            return verificarNumeroSeguridadSocial(dato);
        case 'DNI_CIF':
            if (tabla === 'EMPLEADOS') {
                return verificarDNI(dato);
            } else {
                return verificarCIF(dato);
            }
        case 'MAIL':
            return verificarEmail(dato);
        case 'CODIGO_POSTAL':
            return verificarCodigoPostal(dato);
        case 'PRECIO_COMPRA': case 'PRECIO': case 'SUELDO_BRUTO': case 'PESO':
            return verificarDecimal(dato);
        case 'FECHA_YEAR':
            return verificarYear(dato);
        case 'FECHA_INICIO_VENTA': case 'FECHA': case 'FECHA_INICIO': case 'FECHA_FINAL':
            return verificarFecha(dato);
        case 'HORA_ENTRADA': case 'HORA_SALIDA':
            return verificarHora(dato);
        default:
            return true;
    }
}

/**
 * Verifica que el String solo contenga letras
 * @param {string} dato Contiene el String a verificar
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarString(dato) {
    let regex = /^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ\s]+$/;
    return regex.test(dato.trim());
}

/**
 * Verifica el dato de teléfono fijo que contenga 9 dígitos y un prefijo válido.
 * @param {string | number} dato Contiene el número de teléfono.
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarTelefono(dato) {
    let regex = /^[0-9]{9}$/; // Solo 9 dígitos numéricos
    let prefijos_regex3 = new Set(['945', '967', '965', '966', '950', '984', '985',
        '920', '924', '971', '947', '927', '956', '942', '964', '926', '957', '981',
        '969', '972', '958', '949', '943', '959', '974', '953', '987', '973', '982',
        '951', '952', '968', '948', '988', '979', '928', '986', '941', '923', '921',
        '954', '955', '975', '977', '922', '978', '925', '960', '961', '962', '963',
        '983', '944', '946', '980', '976'
    ]);
    let prefijos_regex2 = new Set(['93', '91']);

    let prefijo3 = dato.substring(0, 3);
    let prefijo2 = dato.substring(0, 2);

    return regex.test(dato.trim()) && (prefijos_regex3.has(prefijo3) || prefijos_regex2.has(prefijo2));
}
/**
 * Verifica el dato de movil que contenga 9 dígitos y que empieza por 6 o 7.
 * @param {string | number} dato Contiene el número de movil.
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarMovil(dato) {
    let regex = /^[0-9]{9}$/; // Solo 9 dígitos numéricos
    let prefijos = new Set(['6', '7']);
    let prefijo = dato.substring(0, 1);

    return regex.test(dato.trim()) && prefijos.has(prefijo);
}

/**
 * Verifica que el dato de vacaciones no sea inferior a 0 ni mayor de 30.
 * @param {int} dato Contiene el valor de las vacaciones.
 * @returns Devuelve verdadero si es válido, sino falso.
 */
function verificarVacaciones(dato) {
    let regex = /^[0-9]{2}$/;
    if (dato < 0 || dato > 30) {
        return false;
    }

    return regex.test(dato);
}

/**
 * Verifica si un número de cuenta IBAN es válido según el algoritmo de validación.
 * @param {string} dato Número de cuenta IBAN completo.
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarNumeroCuenta(dato) {
    dato = String(dato).trim(); // Aseguramos que sea una cadena y eliminamos espacios

    if (dato.length !== 24) return false; // Longitud estándar IBAN España (ES)

    let iban = dato.substring(0, 2);    // Letras del IBAN
    let numeroIban = dato.substring(2, 4); // Dígitos de control IBAN
    let numero_cuenta = dato.substring(4); // Número de cuenta sin IBAN

    // Mapeo de letras a números según estándar IBAN
    let pesoLetras = {};
    for (let i = 0; i < 26; i++) {
        let letra = String.fromCharCode(65 + i); // 'A' = 65, 'B' = 66, ..., 'Z' = 90
        pesoLetras[letra] = (10 + i).toString();
    }

    // Convertir las letras IBAN a sus valores numéricos
    let valorIban = "";
    for (let i = 0; i < iban.length; i++) {
        let c = iban[i];
        if (pesoLetras[c]) {
            valorIban += pesoLetras[c];
        } else {
            return false; // Si hay caracteres inválidos
        }
    }

    // Concatenamos el número de cuenta y el IBAN convertido
    let numeroParaCalcularStr = numero_cuenta + valorIban + numeroIban;

    // Convertimos la cadena en un BIGINT y calculamos módulo 97
    let numeroGrande = BigInt(numeroParaCalcularStr);
    let modulo = Number(numeroGrande % BigInt(97));

    return modulo === 1; // El IBAN es válido si el módulo es 1
}

/**
 * Verifica que el número de la seguridad social sea válido y contenga 12 dígitos.
 * @param {string | number} dato Contiene el número de la seguridad social introducido por el usuario.
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarNumeroSeguridadSocial(dato) {
    dato = String(dato).trim(); // Aseguramos que sea una cadena y eliminamos espacios

    if (!/^\d{12}$/.test(dato)) return false; // Verifica que solo haya 12 dígitos

    let cod_prov = dato.substring(0, 2); // Código de provincia España (ES)
    let n_afiliado = dato.substring(2, 10); // Número de afiliado
    let n_control = parseInt(dato.substring(10), 10); // Número de control

    let n_test = parseInt(n_afiliado, 10);
    if (n_test < 10000000) {
        n_test = n_test + parseInt(cod_prov, 10) * 10000000;
    } else {
        n_test = parseInt(cod_prov + n_afiliado, 10);
    }

    let validacion = n_test % 97;

    return validacion === n_control;
}

/**
 * Verifica que el DNI introducido por el usuario sea el correcto.
 * @param {string} dato Contiene el número de DNI con la letra introducido por el usuario.
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarDNI(dato) {
    dato = String(dato).trim().toUpperCase();;// Aseguramos que sea una cadena y eliminamos espacios

    if (!/^\d{7,8}[A-Z]$/.test(dato)) return false; // Verifica formato correcto: 7 u 8 dígitos + 1 letra

    let n_DNI = parseInt(dato.substring(0, (dato.length - 1))); // Obtener el número
    let letra = dato.substring(dato.length - 1); // Obtener la letra
    
    let letras_DNI = ["T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V",
        "H", "L", "C", "K", "E"];

    let indice_letra = n_DNI % 23;

    let letra_correcta = letras_DNI[indice_letra];

    return letra_correcta === letra;
}

/**
 * Verifica que el CIF introducido por el usuario sea el correcto.
 * @param {string} dato Contiene el número de CIF con la letra introducido por el usuario.
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarCIF(dato) {
    dato = dato.toUpperCase();
    if (!/^[ABCDEFGHJNPQRSUVW]\d{7}[0-9A-J]$/.test(dato)) return false; // Validar estructura general

    let letras = "JABCDEFGHI"; // Letras de control posibles
    let letraInicio = dato.charAt(0);
    let numeros = dato.substr(1, 7);
    let digitoControl = dato.charAt(8);

    let sumaPar = 0;
    let sumaImpar = 0;

    // Cálculo del dígito de control
    for (let i = 0; i < 7; i++) {
        let num = parseInt(numeros[i]);
        if (i % 2 === 0) {
            let doble = num * 2;
            sumaImpar += doble > 9 ? doble - 9 : doble; // Sumar los dígitos del resultado
        } else {
            sumaPar += num;
        }
    }

    let sumaTotal = sumaPar + sumaImpar;
    let digitoCalculado = (10 - (sumaTotal % 10)) % 10;

    // Si la letra de inicio es N, P, Q, R o S → debe llevar una letra de control
    if ("NPQRS".includes(letraInicio)) {
        return digitoControl === letras[digitoCalculado];
    }

    // Si la letra de inicio es A, B, E, H, K → debe llevar un número de control
    if ("ABEH".includes(letraInicio)) {
        return parseInt(digitoControl) === digitoCalculado;
    }

    // Para los demás casos, el dígito de control puede ser tanto numérico como letra
    return parseInt(digitoControl) === digitoCalculado || digitoControl === letras[digitoCalculado];
}

/**
 * Verifica si un correo electrónico tiene un formato válido.
 * @param {string} dato - Dirección de correo a validar.
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarEmail(dato) {
    let regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(dato.trim());
}

/**
 * Verifica que el código postal sea el correcto.
 * @param {string} dato - Contiene el valor de código postal
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarCodigoPostal(dato) {
    let regex = /^(?:0?[1-9]|[1-4]\d|5[0-2])\d{3}$/;
    return regex.test(dato.trim());
}

/**
 * Verifica que sea un número decimal con 2 dígitos.
 * @param {string} dato - Contiene el valor de decimal
 * @returns {boolean} Devuelve verdadero si es válido, sino falso.
 */
function verificarDecimal(dato) {
    let regex = /^\d+\.\d{2}$/;
    return regex.test(dato.trim());
}

/**
 * Verifica que el valor sea un año válido entre 1900 y 2099.
 * @param {string} dato - Contiene el año en formato string.
 * @returns {boolean} Devuelve verdadero si el año es válido.
 */
function verificarYear(dato) {
    let regex = /^(19|20)\d{2}$/;
    return regex.test(dato.trim());
}

/**
 * Verifica que el valor sea una fecha en formato YYYY-MM-DD.
 * Acepta años entre 1900 y 2099, meses entre 01 y 12 y días entre 01 y 31.
 * No valida si el día existe en ese mes (ej. no valida si febrero tiene 29).
 * @param {string} dato - Contiene la fecha.
 * @returns {boolean} Devuelve verdadero si el formato es correcto.
 */
function verificarFecha(dato) {
    let regex = /^(19|20)\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;
    return regex.test(dato.trim());
}

/**
 * Verifica que el valor sea una hora en formato HH:mm:ss (hora de 24 horas).
 * Acepta horas entre 00 y 23, minutos y segundos entre 00 y 59.
 * @param {string} dato - Contiene la hora.
 * @returns {boolean} Devuelve verdadero si el formato es correcto.
 */
function verificarHora(dato) {
    let regex = /^(?:[01]\d|2[0-3]):[0-5]\d:[0-5]\d$/;
    return regex.test(dato.trim());
}