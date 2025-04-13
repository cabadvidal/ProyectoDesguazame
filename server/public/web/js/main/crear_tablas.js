
/**
 * Selecciona el tipo de acción a realizar
 * @param {String} dato Contiene la acción que se quiere realizar
 * @param {String} menu_seleccionado Contiene el nombre de la tabla que se quiere consultar
 */
async function realizarAccionNav(dato, menu_seleccionado) {
    let accion = dato.trim().split(' ')[0];
    accion = accion.toLowerCase();
    menu_seleccionado = menu_seleccionado.replace('í', 'i');
    let datos = null;
    switch (accion) {
        case 'agregar':
            datos = await obtenerNombresColumnas(menu_seleccionado.toUpperCase());
            if (datos) {
                crearTablaAgregar(datos, menu_seleccionado);
            }
            break;
        case 'consultar':
            datos = await consultarDatos(menu_seleccionado.toUpperCase());
            if (datos) {
                crearTablaConsulta(datos, menu_seleccionado.toUpperCase());
            }
            break;
        case 'eliminar':
            eliminarDatos(menu_seleccionado.toUpperCase());
            break;
        case 'modificar':
            modificarDatos(menu_seleccionado.toUpperCase());
            break;
        case 'fichar':
            ficharEmpleado();
            break;
        case 'ver':
            verDatos(menu_seleccionado.toUpperCase());
            break;
        case 'solicitar':
            solicitarAccion(menu_seleccionado.toUpperCase());
            break;
        default:
            console.warn(`❌ Acción no reconocida: ${accion}`);
            popUpError(`❌ Acción no reconocida: ${accion}`);
            break;
    }

    if (datos = null) {
        popUpError(`❌ Error al obtener datos sobre la tabla: ${menu_seleccionado}`)
    }
}


/**
 * Crea una tabla HTML con los nombres de las columnas proporcionadas y permite que las claves foráneas
 * sean seleccionadas a través de un `<select>` y el resto de las columnas a través de `<input>`.
 * 
 * @param {Array<string>} columnas - Array de nombres de columnas, donde cada nombre de columna puede 
 * incluir prefijos como 'FK' para claves foráneas y 'SS' para 'SEG. SOCIAL'.
 * @param {String} tabla - Contiene el nombre de la tabla a agregar datos.
 */
async function crearTablaAgregar(columnas, tabla) {
    console.log(columnas);
    let div_table_container = document.getElementById('table-container');

    div_table_container.innerHTML = '';
    let div;
    for (let i = 0; i < columnas.length; i++) {
        if (i % 3 === 0) {
            div = document.createElement('div');
            div.className = `class-name-agregar-${tabla.toLowerCase()}`;
        }
        let span = document.createElement('span');
        let p = document.createElement('p');

        let input = document.createElement('input');
        input.name = columnas[i].COLUMN_NAME;
        switch (columnas[i].COLUMN_NAME) {
            case 'CONTRASENA':
                p.innerHTML = 'CONTRASEÑA';
                input.placeholder = 'CONTRASEÑA';
                input.type = 'password';
                break;
            case 'DNI_CIF':
                input.placeholder = (tabla === 'EMPLEADOS' || tabla === 'CLIENTE') ? 'DNI' : 'CIF';
                p.innerHTML = (tabla === 'EMPLEADOS' || tabla === 'CLIENTE') ? 'DNI' : 'CIF';
                break;
            default:
                p.innerHTML = columnas[i].COLUMN_NAME.replace(/_A/g, '').replace(/_B/g, '').replace(/_/g, ' ').replace('FK', '').replace('SS', 'SEG. SOCIAL');
                input.placeholder = columnas[i].COLUMN_NAME.replace(/_A/g, '').replace(/_B/g, '').replace(/_/g, ' ').replace('FK', '').replace('SS', 'SEG. SOCIAL');
                break;
        }

        span.appendChild(p);

        if (columnas[i].COLUMN_NAME.includes('TIPO_CUENTA')) {
            let select = document.createElement('select');
            select.innerHTML = 'Seleccione una opción';
            select.name = columnas[i].COLUMN_NAME;
            for (let j = 0; j < 2; j++) {
                let option = document.createElement('option');
                option.innerHTML = j === 1 ? 'ADM' : 'GESTOR';
                select.append(option);
            }
            span.appendChild(select);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME.includes('NOMBRE_MARCA') && tabla.toUpperCase() === 'MODELOS') {
            let select = document.createElement('select');
            select.innerHTML = 'Seleccione una marca';
            select.name = columnas[i].COLUMN_NAME;
            let marcas = await obtenerDatosFK(tabla);
            console.log(JSON.stringify(marcas));
            marcas.forEach(marca => {
                let option = document.createElement('option');
                option.value = marca.ID_MARCAS;
                option.innerHTML = marca.NOMBRE_MARCA;
                select.append(option);
            });
            span.appendChild(select);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        span.appendChild(input);
        div.appendChild(span);
        div_table_container.appendChild(div);
    }

    let button = document.createElement('button');
    button.innerHTML = 'Agregar';
    button.className = 'class-button-agregar';

    button.addEventListener('click', async function (event) {
        let datos = obtenerDatosAgregar(tabla.toLowerCase());
        console.log(datos);
        let isDatos = verificarDatos(datos, tabla.toUpperCase());
        let resultado = null;
        if (isDatos.isCorrecto) {
            console.log(`Datos correctos`);
            resultado = await agregarDatos(datos, tabla.toUpperCase());
            popUpError(`✅ ${resultado.mensaje}`);
        } else {
            popUpError(`Los datos no son correctos en ${isDatos.key}`)
        }
    });
    // Agregar la tabla al contenedor    
    div_table_container.appendChild(button);
}

/**
 * Obtiene los datos que quiere agregar el usuario
 * @param {string} table contiene el nombre de la tabla
 * @returns Devuelve un objecto JSON con los datos del formulario
 */
function obtenerDatosAgregar(table) {
    // Seleccionamos todos los divs que coinciden con la clase correspondiente
    let divs = Array.from(document.querySelectorAll('div')).filter(div => div.classList.contains(`class-name-agregar-${table}`));
    let filaDatos = {};
    divs.forEach(div => {
        // Seleccionar todos los span dentro del div
        let spans = div.querySelectorAll('span');

        spans.forEach(span => {
            let input = span.querySelector('input');
            let select = span.querySelector('select');

            if (input) {
                filaDatos[input.name] = input.value;
            }
            if (select) {
                filaDatos[select.name] = select.value;
            }
        });
    });
    console.log("Datos recogidos:", filaDatos);
    return filaDatos;
}

/**
 * Función que crea la tabla con los datos a mostrar
 * @param {Object} datos Contiene los datos a mostrar en un objecto JSON.
 * @param {String} tabla Contiene el nombre de la tabla a la que hacen referencia los datos.
 */
async function crearTablaConsulta(datos, tabla) {
    let div_table_container = document.getElementById('table-container');
    div_table_container.innerHTML = '';  // Limpiar el contenedor de la tabla

    let table = document.createElement('table');
    table.className = `class-name-consultar`;  // Asignar una clase a la tabla

    // Crear el encabezado de la tabla
    let table_head = document.createElement('thead');
    let tr = document.createElement('tr');
    let marcas, categorias, modelos, empleados, vendedores, clientes;
    if (tabla.toUpperCase().includes('MODELOS') || tabla.toUpperCase().includes('PIEZAS')) {
        marcas = await obtenerDatosFK('MARCAS');
    }

    if (tabla.toUpperCase().includes('PIEZAS')) {
        categorias = await obtenerDatosFK('CATEGORIAS');
        modelos = await obtenerDatosFK('MODELOS');
        empleados = await obtenerDatosFK('EMPLEADOS');
        vendedores = await obtenerDatosFK('VENDEDOR');
        clientes = await obtenerDatosFK('CLIENTE');
    }
    console.log(`Los datos son ${JSON.stringify(datos[0])}`)
    // Iterar sobre las claves de los primeros datos para crear el encabezado
    Object.keys(datos[0]).forEach(key => {
        let th = document.createElement('th');
        if (key.includes('ID_PIEZAS_MODELOS')) { return }
        if (key !== 'DNI_CIF') {
            th.innerHTML = key.replace(/_A/g, '').replace(/_B/g, '').replace(/_/g, ' ').replace('FK', '').replace('SS', 'SEG. SOCIAL').replace('FECHA YEAR', 'AÑO'); // Limpiar el nombre de las columnas
        } else {
            th.innerHTML = (tabla === 'EMPLEADOS' || tabla === 'CLIENTE') ? 'DNI' : 'CIF';
        }

        tr.appendChild(th);
    });

    for (let i = 0; i < 2; i++) {
        let th = document.createElement('th');
        th.innerHTML = i === 0 ? 'MODIFICAR' : 'ELIMINAR';
        tr.appendChild(th);
    }

    table_head.appendChild(tr);
    table.appendChild(table_head);

    // Crear el cuerpo de la tabla
    let table_body = document.createElement('tbody');
    datos.forEach(fila => {
        let tr_body = document.createElement('tr');
        let elementosFila = [];  // Guardar referencias a los inputs y selects

        Object.keys(fila).forEach(async key => {

            if ((key.includes('CATEGORIAS_FK') || key.includes('VENDEDOR_FK') ||
                key.includes('EMPLEADO_FK') || key.includes('MODELO_FK')) && tabla.toUpperCase() === 'PIEZAS') {
                return;
            }

            let td = document.createElement('td');
            let elemento;

            elemento = document.createElement('input');
            elemento.value = fila[key];
            elemento.disabled = true;
            if (key.includes('TIPO_CUENTA')) {
                elemento = document.createElement('select');

                let option1 = document.createElement('option');
                option1.value = fila[key];
                option1.textContent = fila[key];
                elemento.appendChild(option1);

                let option2 = document.createElement('option');
                option2.value = fila[key] === 'ADM' ? 'GESTOR' : 'ADM';
                option2.textContent = option2.value;
                elemento.appendChild(option2);

                elemento.disabled = true;
                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('NOMBRE_MARCA') && (tabla.toUpperCase() === 'MODELOS' || tabla.toUpperCase().includes('PIEZAS'))) {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                let marcaActual = marcas.find(marca => marca.NOMBRE_MARCA === fila[key]);
                let idDefecto = marcaActual ? marcaActual.ID_MARCAS : '';
                valorDefecto.value = idDefecto;
                valorDefecto.innerHTML = fila[key];
                elemento.appendChild(valorDefecto);
                marcas.forEach(marca => {
                    let option = document.createElement('option');
                    option.value = marca.ID_MARCAS;
                    option.innerHTML = marca.NOMBRE_MARCA;
                    elemento.append(option);
                });
                elemento.disabled = true;

                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('CATEGORIAS') && tabla.toUpperCase() === 'PIEZAS') {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                let categoriaActual = categorias.find(categoria => categoria.NOMBRE === fila[key]);
                let idDefecto = categoriaActual ? categoriaActual.ID_CATEGORIAS_PIEZAS : '';
                valorDefecto.value = idDefecto;
                valorDefecto.innerHTML = fila[key];
                elemento.appendChild(valorDefecto);
                categorias.forEach(categoria => {
                    let option = document.createElement('option');
                    option.value = categoria.ID_CATEGORIAS_PIEZAS;
                    option.innerHTML = categoria.NOMBRE;
                    elemento.append(option);
                });
                elemento.disabled = true;

                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('RAZON_SOCIAL') && tabla.toUpperCase() === 'PIEZAS') {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                let vendedorActual = vendedores.find(vendedor => vendedor.RAZON_SOCIAL === fila[key]);
                let idDefecto = vendedorActual ? vendedorActual.ID_VENDEDOR : '';
                valorDefecto.value = idDefecto;
                valorDefecto.innerHTML = fila[key];
                elemento.appendChild(valorDefecto);
                vendedores.forEach(vendedor => {
                    let option = document.createElement('option');
                    option.value = vendedor.ID_VENDEDOR;
                    option.innerHTML = vendedor.RAZON_SOCIAL;
                    elemento.append(option);
                });
                elemento.disabled = true;

                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('EMPLEADO') && tabla.toUpperCase() === 'PIEZAS') {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                console.log(`Datos de empleado ${JSON.stringify(empleados)}`);
                let empleadoActual = empleados.find(empleado => empleado.DNI_CIF === fila[key]);
                let idDefecto = empleadoActual ? empleadoActual.ID_EMPLEADOS : '';
                valorDefecto.value = idDefecto;
                valorDefecto.innerHTML = fila[key];
                elemento.appendChild(valorDefecto);
                empleados.forEach(empleado => {
                    let option = document.createElement('option');
                    option.value = empleado.ID_EMPLEADOS;
                    option.innerHTML = empleado.DNI_CIF;
                    elemento.append(option);
                });
                elemento.disabled = true;

                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('NOMBRE_MODELO') && tabla.toUpperCase() === 'PIEZAS') {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                let modeloActual = modelos.find(modelo => modelo.NOMBRE_MODELO === fila[key]);
                let idDefecto = modeloActual ? modeloActual.ID_MODELO : '';
                valorDefecto.value = idDefecto;
                valorDefecto.innerHTML = fila[key];
                elemento.appendChild(valorDefecto);
                modelos.forEach(modelo => {
                    let option = document.createElement('option');
                    option.value = modelo.ID_MODELO;
                    option.innerHTML = modelo.NOMBRE_MODELO;
                    elemento.append(option);
                });
                elemento.disabled = true;

                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('CLIENTE') && tabla.toUpperCase() === 'PIEZAS') {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                let clienteActual = clientes.find(cliente => cliente.DNI_CIF === fila[key]);
                let idDefecto = clienteActual ? clienteActual.ID_CLIENTE : '';
                valorDefecto.value = idDefecto;
                valorDefecto.innerHTML = fila[key];
                elemento.appendChild(valorDefecto);
                clientes.forEach(cliente => {
                    let option = document.createElement('option');
                    option.value = cliente.ID_CLIENTE;
                    option.innerHTML = cliente.DNI_CIF;
                    elemento.append(option);
                });
                elemento.disabled = true;

                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('ID_PIEZAS_MODELOS') && tabla.toUpperCase() === 'PIEZAS') {
                elemento = document.createElement('input');
                elemento.value = fila[key];
                elemento.disabled = true;
                elemento.type = 'hidden';
                td.appendChild(elemento);
                //tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            td.appendChild(elemento);
            tr_body.appendChild(td);
            elementosFila.push(elemento);
        });

        let idFila = Object.values(fila)[0];

        // Crear los botones de modificar y eliminar
        let td_Mod = document.createElement('td');
        let button_Mod = document.createElement('button');
        button_Mod.innerHTML = '✏️';  // Ícono de lápiz para modificar
        button_Mod.addEventListener('click', () => {
            if (button_Mod.innerHTML === '✏️') {
                button_Mod.innerHTML = '💾';  // Cambiar a icono de guardar
                elementosFila.forEach((elemento, index) => {
                    if (index !== 0) {  // Evitar que el primer elemento se habilite
                        elemento.disabled = false;
                    }
                });  // Habilitar inputs 
            } else {
                button_Mod.innerHTML = '✏️';  // Volver al lápiz
                elementosFila.forEach(elemento => elemento.disabled = true);  // Deshabilitar edición

                // Obtener nuevos valores del input y select
                let nuevosDatos = elementosFila.map(el => el.value);
                let datosModificar = {};
                // Agregar los datos modificados como un JSON
                Object.keys(datos[0]).forEach((key, index) => {
                    if (key === 'NOMBRE_MARCA' && tabla.toUpperCase() === 'MODELOS') {
                        datosModificar['MARCA_MODELO_FK'] = nuevosDatos[index].trim();
                        return;
                    }
                    datosModificar[key] = nuevosDatos[index].trim();
                });
                popUpError(actualizarDatos(datosModificar, tabla));
                console.log(`Guardando cambios para ID: ${idFila}`, JSON.stringify(datosModificar));
            }
        });

        td_Mod.appendChild(button_Mod);
        tr_body.appendChild(td_Mod);

        let td_Del = document.createElement('td');
        let button_Del = document.createElement('button');
        button_Del.innerHTML = '❌';  // Ícono de eliminar
        button_Del.addEventListener('click', () => {
            // Lógica para eliminar el registro
            console.log(`Eliminar registro con ID: ${idFila}`);
        });
        td_Del.appendChild(button_Del);
        tr_body.appendChild(td_Del);

        table_body.appendChild(tr_body);
    });

    table.appendChild(table_body);
    div_table_container.appendChild(table);  // Agregar la tabla al contenedor
}

function obtenerDatosFila(datos, idFila) {
    let resultado = {};

    resultado = datos[idFila];
    return resultado;
}