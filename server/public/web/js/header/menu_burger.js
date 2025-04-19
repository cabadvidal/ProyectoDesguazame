document.addEventListener("DOMContentLoaded", function () {
    createMenuDisplay();

    // Mostrar/ocultar el menú principal al hacer clic en el botón
    document.getElementById("menu_button").addEventListener("click", function (event) {
        event.stopPropagation(); // Evita que el clic se propague y cierre el menú
        let menuContainer = document.getElementById("menu_container");
        menuContainer.classList.toggle("hidden");
        closeSubMenu(); // Cierra el submenú cuando se abre el menú principal
    });

    // Cierra los menús al hacer clic fuera de ellos
    document.addEventListener("click", function () {
        closeMenu();
        closeSubMenu();
    });

    // Evita cerrar el menú si se hace clic dentro del contenedor de menú
    document.getElementById("menu_container").addEventListener("click", function (event) {
        event.stopPropagation();
    });

    // Evita cerrar el submenú si se hace clic dentro del contenedor de submenú
    document.getElementById("submenu_container").addEventListener("click", function (event) {
        event.stopPropagation();
    });
});

function createMenuDisplay() {
    let div_container = document.getElementById("menu_container");
    div_container.innerHTML = '';
    let div_menu = document.createElement('div');
    div_menu.className = "menu_display";
    let ul = document.createElement('ul');
    ul.className = "ul_menu";
    let menu_data = [];
    if (tipo_cuenta !== 'ADM') {
        menu_data = ['Piezas', 'Vendedores', 'Marcas', 'Modelos', 'Categorías', 'Fichar', 'Nominas', 'Vacaciones', 'Facturas'];
    } else {
        menu_data = ['Piezas', 'Empleados', 'Vendedores', 'Marcas', 'Modelos', 'Categorías', 'Fichar', 'Nominas', 'Vacaciones', 'Facturas'];
    }

    for (let i = 0; i < menu_data.length; i++) {
        let li = document.createElement('li');
        li.className = 'class_menu_li';
        li.innerText = menu_data[i];
        li.addEventListener('click', function (event) {
            event.stopPropagation(); // Evita que el clic cierre el menú principal
            addSubMenu(menu_data[i]);
        });
        ul.appendChild(li);
    }

    div_menu.appendChild(ul);
    div_container.appendChild(div_menu);
}

function addSubMenu(option) {
    let option_submenu = [];
    switch (option) {
        case 'Piezas':
            option_submenu = ['Agregar piezas', 'Consultar piezas'];
            break;
        case 'Empleados':
            if (tipo_cuenta !== 'ADM') return;
            option_submenu = ['Agregar empleados', 'Consultar empleados'];
            break;
        case 'Vendedores':
            option_submenu = ['Agregar vendedores', 'Consultar vendedores'];
            break;
        case 'Marcas':
            option_submenu = ['Agregar marcas', 'Consultar marcas'];
            break;
        case 'Modelos':
            option_submenu = ['Agregar modelos', 'Consultar modelos'];
            break;
        case 'Categorías':
            option_submenu = ['Agregar categorías', 'Consultar categorías'];
            break;
        case 'Fichar':
            option_submenu = (tipo_cuenta === 'ADM') ? ['Gestionar Fichajes'] : ['Fichar', 'Ver Fichajes'];
            break;
        case 'Nominas': {
            option_submenu = (tipo_cuenta === 'ADM') ? ['Agregar Nomina', 'Consultar Nominas'] : ['Ver Nominas'];
            break;
        }
        case 'Vacaciones': {
            option_submenu = (tipo_cuenta === 'ADM') ? ['Gestionar Vacaciones'] : ['Ver Vacaciones', 'Solicitar Vacaciones'];
            break;
        }
        case 'Facturas': {
            option_submenu = ['Consultar Facturas'];
            break;
        }
    }

    let div_container = document.getElementById("submenu_container");
    div_container.classList.remove("hidden"); // Mostrar el submenú

    let div_submenu = document.getElementById('div_submenu');
    if (!div_submenu) {
        div_submenu = document.createElement('div');
        div_submenu.id = 'div_submenu';
        div_submenu.className = 'div_submenu';
        div_container.appendChild(div_submenu); // Solo lo añadimos al contenedor si no existe
    } else {
        div_submenu.innerHTML = ''; // Limpiamos el contenido existente
    }

    let ul = document.createElement('ul');
    ul.className = "ul_submenu";
    for (let i = 0; i < option_submenu.length; i++) {
        let li = document.createElement('li');
        li.className = 'class_menu_li';
        li.innerText = option_submenu[i];
        li.addEventListener('click', function (event) {
            event.stopPropagation(); // Evita que el clic cierre el menú principal
            createNavBottom(option_submenu, option);
            realizarAccionNav(option_submenu[i], option);
        });
        ul.appendChild(li);
    }
    div_submenu.appendChild(ul);
}

// Función para cerrar el menú principal
function closeMenu() {
    let menuContainer = document.getElementById("menu_container");
    menuContainer.classList.add("hidden");
}

// Función para cerrar el submenú
function closeSubMenu() {
    let submenuContainer = document.getElementById("submenu_container");
    submenuContainer.classList.add("hidden");
}
