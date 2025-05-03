/**
 * Función que muestra diferentes errores en pantalla
 * @param {string} datos - Contiene el error a mostrar
 */
function popUpError(datos) {
    // Mostrar el fondo negro
    let overlay = document.getElementById('overlay');
    overlay.classList.remove('hidden');

    // Crear el menú de login
    let error_menu = document.getElementById('error_menu_container');
    error_menu.classList.remove('hidden');
    error_menu.innerHTML = ''; // Limpiar contenido existente

    // Crear el botón de cerrar (X)
    let close_button = document.createElement('span');
    close_button.className = 'close_button';
    close_button.innerText = '✖';
    close_button.addEventListener('click', function () {
        overlay.classList.add('hidden');
        error_menu.classList.add('hidden');
    });
    error_menu.appendChild(close_button);

    // Crear div que contendrá el menu donde se muestra el error en una etiqueta p
    let div_error = document.createElement('div');
    div_error.id = 'error_menu';

    let p_error = document.createElement('p');
    p_error.textContent = datos;

    div_error.appendChild(p_error);
    error_menu.appendChild(div_error);
}