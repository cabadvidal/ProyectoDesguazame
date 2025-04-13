/**
 * Crea el menú con los botones para acceder a las diferentes secciones
 * @param {Array} array Contiene un array con las diferentes acciones
 * @param {String} menu_seleccionado Contiene la opción del menú seleccionado por el usuario
 */
function createNavBottom(array, menu_seleccionado){
    let nav = document.getElementsByClassName('subnav_container')[0];
    nav.innerHTML = '';
    let ul = document.createElement('ul');
    ul.id = 'ul_subnav';

    for(let i = 0; i < array.length; i++) {
        let li = document.createElement('li');
        li.id = 'li_' + array[i].toLowerCase().replace(' ', '_').replace('á', 'a')
                                .replace('é', 'e').replace('í', 'i').replace('ó','o').replace('ú', 'u');
        li.innerText = array[i];
        ul.appendChild(li);
        li.addEventListener('click', async function(event){ realizarAccionNav(array[i], menu_seleccionado)});
    }
    nav.appendChild(ul);
}

/**
 * Crea la interfaz para poder deslogarse con mensaje de bienvenida
 */
function createLogin(nombre, apellidos) {
    let divLogin = document.getElementById('login_button');
    let divWel = document.getElementById('welcome-message');
    divWel.innerHTML = '';

    let p = document.createElement('p');
    p.innerHTML = `Bienvenido/a ${apellidos}, ${nombre}`;
    let button = document.createElement('button');

    button.innerHTML = 'Deslogarse';

    button.addEventListener('click', function(event) {
        console.warn("🔴 Desconectado del servidor.");
        socket.emit("desconectar", token);
        p.innerHTML = '';
        button.remove();
        createMenuLogin();
    });

    divWel.appendChild(p);
    divLogin.appendChild(button);
}