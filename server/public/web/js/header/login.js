var global_session = null;
var socket = io({ autoConnect: true });

var token = getCookie("user_token") || 0; // Obtener el token almacenado en cookies
var nombre = getCookie("nombre" || "");
var apellidos = getCookie("apellidos" || "");
var tipo_cuenta = getCookie("tipo_cuenta" || "");

function createMenuLogin() {
    // Mostrar el fondo negro
    let overlay = document.getElementById('login_overlay');
    overlay.classList.remove('hidden');

    // Obtener el container del menu
    let login_menu = document.getElementById('login_menu_container');
    login_menu.classList.remove('hidden');
    login_menu.innerHTML = ''; // Limpiar contenido existente

    // Crear el botón de cerrar (X)
    /*  let close_button = document.createElement('span');
      close_button.className = 'close_button';
      close_button.innerText = '✖';
      close_button.addEventListener('click', function () {
          overlay.classList.add('hidden');
          login_menu.classList.add('hidden');
      });
      login_menu.appendChild(close_button); */

    // Crear el div del menu
    let div_menu = document.createElement('div');
    div_menu.id = 'login_menu';
    login_menu.appendChild(div_menu);

    // Crear elementos de usuario
    let span_name = document.createElement('span');
    span_name.id = 'span_name';
    let p_name = document.createElement('p');
    p_name.id = 'p_name';
    p_name.innerText = 'Correo electrónico';
    span_name.appendChild(p_name);

    let input_name = document.createElement('input');
    input_name.id = 'input_name';
    input_name.placeholder = "Correo electrónico";

    // Crear elementos de contraseña
    let span_pass = document.createElement('span');
    span_pass.id = 'span_pass';
    let p_pass = document.createElement('p');
    p_pass.id = 'p_pass';
    p_pass.innerText = 'Contraseña';
    span_pass.appendChild(p_pass);

    let input_pass = document.createElement('input');
    input_pass.id = 'input_pass';
    input_pass.placeholder = 'Contraseña';
    input_pass.type = 'password';

    let button_login = document.createElement('button');
    button_login.id = 'button_login';
    button_login.innerText = "Acceder";
    button_login.addEventListener('click', function (event) {
        let email = input_name.value;
        let pass = input_pass.value;
        let datos = {
            "usuario": email,
            "password": pass
        }
        socket.emit("comprobar credenciales", datos);


    });

    let img = document.createElement('img');
    img.src = 'img/menu/coche-transparente.png';
    img.id = 'cochecitolere';

    // Añadir todo al menú de login
    div_menu.appendChild(img);
    div_menu.appendChild(span_name);
    div_menu.appendChild(input_name);
    div_menu.appendChild(span_pass);
    div_menu.appendChild(input_pass);
    
    div_menu.appendChild(button_login);
    //div_menu.appendChild(button_google);
}

document.addEventListener("DOMContentLoaded", function () {
    // Esperar a que la conexión esté lista
    socket.on("connect", function () {
        console.log("✅ Conectado al servidor Socket.io");

        // Enviar el token al servidor para verificar la sesión
        if (token) {
            console.log("🔄 Verificando sesión con token:", token);
            socket.emit("reconexion", token);
        } else {
            console.log("⚠️ No hay token almacenado, requiere login.");
            createMenuLogin();
        }
    });
});

socket.on("token_empleado", async (datos) => {
    console.log(datos);
    if (datos.nombre !== null & datos.token !== null) {
        token = datos.token;
        nombre = datos.nombre;
        apellidos = datos.apellidos;
        tipo_cuenta = datos.tipo_cuenta;
        // Guardar el token en cookies por 1 día
        setCookie("user_token", token, 1);
        // Quita el Overlay y el menú de login
        let overlay = document.getElementById('login_overlay');
        let login_menu = document.getElementById('login_menu_container');
        let div_menu = document.getElementById('login_menu');
        overlay.classList.add('hidden');
        login_menu.classList.add('hidden');
        login_menu.innerHTML = '';
        div_menu.innerHTML = '';
        createMenuDisplay();
        setCookie("nombre", nombre, 1);
        setCookie("apellidos", apellidos, 1);
        setCookie("tipo_cuenta", tipo_cuenta, 1);
        createLogin(nombre, apellidos);
    } else {
        //alert('❌ Error login');
        popUpError('❌ Error usuario o contraseña incorrectos.');
        createMenuLogin();
    }
});

socket.on("token", async (datos) => {
    console.log(datos);
    if (!datos) {
        popUpError('❌ Error usuario o contraseña incorrectos.');
        setTimeout(() => {
            createMenuLogin();
        }, 5000);
    } 
});

// Enviar actividad cada minuto para mantener la sesión activa
setInterval(() => {
    if (token) {
        socket.emit("actividad", token);
    }
}, 60000);

function confirmarConexion(actividad) {
    return new Promise((resolve, reject) => {
        if (!token) {
            reject(false);
            return;
        }

        socket.emit("actividad", { token: token, actividad: actividad });

        socket.on('respuesta actividad', function (datos) {
            resolve(datos.valido);
        });

        // Timeout si el servidor tarda mas de 10 seg en responder
        setTimeout(() => reject(false), 10000);
    });
}


// Esperar a que la conexión esté lista
socket.on("connection", function () {
    console.log("✅ Conectado al servidor Socket.io");

    // Enviar el token al servidor para verificar la sesión
    if (token) {
        console.log("🔄 Verificando sesión con token:", token);
        socket.emit("reconexion", token);
    } else {
        console.log("⚠️ No hay token almacenado, requiere login.");
        popUpError('⚠️ No hay datos almacenados, requiere login.');
        createMenuLogin();
    }
});

// Manejar la respuesta del servidor tras la verificación del token
socket.on("respuesta verificación", function (datos) {
    if (datos.valido) {
        console.log("✅ Token válido, sesión iniciada.");
        console.log(`El nombre es: ${nombre} y los apellidos: ${apellidos}`)
        createLogin(nombre, apellidos);
    } else {
        console.log("❌ Token inválido, mostrando login.");
        token = null;
        deleteCookie("user_token"); // Eliminar token inválido
        deleteCookie("nombre"); // Eliminar token inválido
        deleteCookie("apellidos"); // Eliminar token inválido
        createMenuLogin(); // Mostrar pantalla de login
    }
});



/**
 * Establece una cookie en el navegador con un valor y una duración específica.
 * 
 * @param {string} name - Nombre de la cookie.
 * @param {string} value - Valor a almacenar en la cookie.
 * @param {number} days - Número de días hasta que la cookie expire.
 */
function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        let date = new Date();
        // Calcula la fecha de expiración en ms
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    // Establece la cookie con el valor y la ruta accesible
    document.cookie = name + "=" + value + expires + "; path=/";
}

/**
 * Obtiene el valor de una cookie específica almacenada en el navegador.
 * 
 * @param {string} name - Nombre de la cookie a obtener.
 * @returns {string|null} - Valor de la cookie si existe, o null si no se encuentra.
 */
function getCookie(name) {
    let nameEQ = name + "=";
    // Divide todas las cookies en un array
    let ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        // Devuelve el valor de la cookie si la encuentra
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

/**
 * Elimina una cookie estableciendo su fecha de expiración en el pasado.
 * 
 * @param {string} name - Nombre de la cookie a eliminar.
 */
function deleteCookie(name) {
    document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
}