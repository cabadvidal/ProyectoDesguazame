
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
    console.log(` Accion: ${accion}`)
    switch (accion) {
        case 'agregar': case 'solicitar':
            datos = await obtenerNombresColumnas(menu_seleccionado.toUpperCase());
            if (datos) {
                crearTablaAgregar(datos, menu_seleccionado);
            }
            break;
        case 'consultar': case 'gestionar':
            datos = await consultarDatos(menu_seleccionado.toUpperCase());
            if (datos) {
                crearTablaConsulta(datos, menu_seleccionado.toUpperCase());
            }
            break;
        case 'modificar':
            modificarPassword();
            break;
        case 'fichar':
            crearFormularioFichar();
            break;
        case 'ver':
            let tipo = dato.trim().split(' ')[1];
            console.log(`Tipo ${tipo}`)
            switch (tipo) {
                case 'Fichajes':
                    datos = await consultarDatos('FICHAJE', { token: token });
                    crearTablaConsulta(datos, 'FICHAR');
                    break;
                case 'Vacaciones':
                    datos = await consultarDatos('VACAS', { token: token });
                    crearTablaConsulta(datos, 'VACACIONES');
                    break;
                case 'Nominas':
                    datos = await consultarDatos('PAGUITA', { token: token });
                    crearTablaConsulta(datos, 'NOMINAS');
                    break;
            }
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
    tabla = tabla.toUpperCase();
    div_table_container.innerHTML = '';
    let div;
    let marcas, empleados, vendedores, modelos, categorias;
    if ((tabla.toUpperCase() === 'MODELOS' || tabla.toUpperCase() === 'PIEZAS')) {
        marcas = await obtenerDatosFK('MARCAS');
    }

    if (tabla.toUpperCase() === 'PIEZAS') {
        empleados = await obtenerDatosFK('EMPLEADOS');
        vendedores = await obtenerDatosFK('VENDEDOR');
        modelos = await obtenerDatosFK('MODELOS');
        categorias = await obtenerDatosFK('CATEGORIAS');
    }

    if (tabla.toUpperCase().includes('NOMINAS')) {
        empleados = await obtenerDatosFK('EMPLEADOS');
    }

    for (let i = 0; i < columnas.length; i++) {
        if (i % 3 === 0) {
            div = document.createElement('div');
            div.className = `class-name-agregar`;
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
                input.name = columnas[i].COLUMN_NAME;
                break;
            case 'DNI_CIF':
                input.placeholder = (tabla === 'EMPLEADOS' || tabla === 'CLIENTE' || tabla === 'PIEZAS') ? 'DNI' : 'CIF';
                p.innerHTML = (tabla === 'EMPLEADOS' || tabla === 'CLIENTE' || tabla === 'PIEZAS' || tabla === 'NOMINAS') ? 'DNI' : 'CIF';
                input.name = columnas[i].COLUMN_NAME;
                break;
            case 'FECHA_YEAR':
                p.innerHTML = 'AÑO';
                input.placeholder = 'AÑO';
                input.type = 'number';
                input.name = columnas[i].COLUMN_NAME;
                break;
            case 'SUELDO_BRUTO':
                p.innerHTML = 'SUELDO BRUTO';
                input.placeholder = 'SUELDO BRUTO';
                input.type = 'number';
                input.step = '0.01';
                input.name = columnas[i].COLUMN_NAME;
                break;
            default:
                p.innerHTML = columnas[i].COLUMN_NAME.replace(/_A/g, '').replace(/_B/g, '').replace(/_/g, ' ').replace('FK', '').replace('SS', 'SEG. SOCIAL');
                input.placeholder = columnas[i].COLUMN_NAME.replace(/_A/g, '').replace(/_B/g, '').replace(/_/g, ' ').replace('FK', '').replace('SS', 'SEG. SOCIAL');
                input.name = columnas[i].COLUMN_NAME;
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

        if (columnas[i].COLUMN_NAME.includes('NOMBRE_MARCA') && (tabla.toUpperCase() === 'MODELOS' || tabla.toUpperCase() === 'PIEZAS')) {
            let select = document.createElement('select');
            select.innerHTML = 'Seleccione una marca';
            select.name = columnas[i].COLUMN_NAME;

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

        if (columnas[i].COLUMN_NAME.includes('DNI_CIF') && (tabla.toUpperCase() === 'PIEZAS' || tabla.toUpperCase() === 'NOMINAS')) {
            let select = document.createElement('select');
            select.innerHTML = 'Seleccione un empleado';
            select.name = 'EMPLEADO_FK';

            empleados.forEach(empleado => {
                let option = document.createElement('option');
                option.value = empleado.ID_EMPLEADOS;
                option.innerHTML = empleado.DNI_CIF;
                select.append(option);
            });
            span.appendChild(select);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME.includes('NOMBRE_MODELO') && tabla.toUpperCase() === 'PIEZAS') {
            // Contenedor principal para los selects de modelos
            let contenedorModelos = document.createElement('div');
            contenedorModelos.className = 'contenedor-modelos';

            // Botón para agregar más selects
            let button_add = document.createElement('button');
            button_add.type = 'button';
            button_add.className = 'btn-add-modelo';
            button_add.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="#007bff" viewBox="0 0 16 16">
                        <circle cx="8" cy="8" r="7" stroke="white" stroke-width="1.5" fill="#007bff" />
                        <line x1="8" y1="4.5" x2="8" y2="11.5" stroke="white" stroke-width="1.5"/>
                        <line x1="4.5" y1="8" x2="11.5" y2="8" stroke="white" stroke-width="1.5"/>
                    </svg>`;

            button_add.addEventListener('click', () => {
                agregarSelect(modelos, contenedorModelos);
            });

            // Añadimos el primer select por defecto
            agregarSelect(modelos, contenedorModelos);

            // Añadir todo al DOM
            span.appendChild(button_add);
            span.appendChild(contenedorModelos);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME.includes('RAZON_SOCIAL') && tabla.toUpperCase() === 'PIEZAS') {
            let select = document.createElement('select');
            select.innerHTML = 'Seleccione un modelo';
            select.name = 'VENDEDOR_FK';

            vendedores.forEach(vendedor => {
                let option = document.createElement('option');
                option.value = vendedor.ID_VENDEDOR;
                option.innerHTML = vendedor.RAZON_SOCIAL;
                select.append(option);
            });
            span.appendChild(select);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME.includes('NOMBRE_CATEGORIA') && tabla.toUpperCase() === 'PIEZAS') {
            let select = document.createElement('select');
            select.innerHTML = 'Seleccione una categoría';
            select.name = 'CATEGORIAS_FK';

            categorias.forEach(categoria => {
                let option = document.createElement('option');
                option.value = categoria.ID_CATEGORIAS_PIEZAS;
                option.innerHTML = categoria.NOMBRE_CATEGORIA;
                select.append(option);
            });
            span.appendChild(select);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME === 'FECHA' || columnas[i].COLUMN_NAME.includes('FECHA_INICIO_VENTA') || columnas[i].COLUMN_NAME.includes('FECHA_INICIO')
            || columnas[i].COLUMN_NAME.includes('FECHA_FINAL')) {
            let inputDate = document.createElement('input');
            inputDate.placeholder = 'Fecha';
            inputDate.type = 'date';
            inputDate.name = columnas[i].COLUMN_NAME;
            span.appendChild(inputDate);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME === 'ESTADO' && tabla.toUpperCase() === 'NOMINAS') {
            let select = document.createElement('select');
            let estado = ['PENDIENTE', 'PAGADA', 'BAJA'];
            select.innerHTML = 'Seleccione un estado';
            select.name = 'ESTADO';

            estado.forEach(stat => {
                let option = document.createElement('option');
                option.value = stat;
                option.innerHTML = stat;
                select.append(option);
            });

            span.appendChild(select);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME === 'MES' && tabla.toUpperCase() === 'NOMINAS') {
            let inputDate = document.createElement('input');
            inputDate.type = 'month';
            inputDate.name = 'MES';
            span.appendChild(inputDate);
            div.appendChild(span);
            div_table_container.appendChild(div);
            continue;
        }

        if (columnas[i].COLUMN_NAME === 'IMAGENES' && tabla.toUpperCase() === 'PIEZAS') {

            // Contenedor principal para los selects de modelos
            let contenedorimagenes = document.createElement('div');
            contenedorimagenes.className = 'contenedor-modelos';

            // Botón para agregar más selects
            let button_add = document.createElement('button');
            button_add.type = 'button';
            button_add.className = 'btn-add-modelo';
            button_add.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="#007bff" viewBox="0 0 16 16">
                        <circle cx="8" cy="8" r="7" stroke="white" stroke-width="1.5" fill="#007bff" />
                        <line x1="8" y1="4.5" x2="8" y2="11.5" stroke="white" stroke-width="1.5"/>
                        <line x1="4.5" y1="8" x2="11.5" y2="8" stroke="white" stroke-width="1.5"/>
                    </svg>`;

            button_add.addEventListener('click', () => {
                agregarImagenes(contenedorimagenes);
            });

            // Añadir todo al DOM
            span.appendChild(button_add);
            span.appendChild(contenedorimagenes);
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
            if (!tabla.includes('PIEZAS')) {
                resultado = await agregarDatos(datos, tabla.toUpperCase());
            } else {
                resultado = await agregarDatosFormData(datos, tabla);
            }
            popUpError(`✅ ${resultado.mensaje}`);
        } else {
            popUpError(`Los datos no son correctos en ${isDatos.key}`)
        }
    });
    // Agregar la tabla al contenedor    
    div_table_container.appendChild(button);
}

/**
 * Crea un nuevo campo <input> para subir imagenes. Permite añadir múltiples imagenes.
 * 
 * @param {HTMLElement} contenedor - El elemento contenedor donde se insertará el inputFile.
 */
function agregarImagenes(contenedor) {
    let div = document.createElement('div'); // contenedor para el select y su botón
    div.className = 'modelo-select-div';

    let inputFile = document.createElement('input');
    inputFile.type = 'file';
    inputFile.name = 'IMAGENES';
    inputFile.accept = 'image/*';
    inputFile.multiple = true;
    // Botón eliminar
    let button_delete = document.createElement('button');
    button_delete.type = 'button';
    button_delete.className = 'btn-remove-modelo';
    button_delete.innerHTML = `
     <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="red" viewBox="0 0 16 16">
         <circle cx="8" cy="8" r="7" stroke="white" stroke-width="1.5" fill="red" />
         <line x1="4.5" y1="8" x2="11.5" y2="8" stroke="white" stroke-width="1.5"/>
     </svg>`;

    button_delete.addEventListener('click', () => {
        div.remove();
    });

    div.appendChild(inputFile);
    div.appendChild(button_delete);
    contenedor.appendChild(div);
}


/**
 * Crea un nuevo campo <select> con una lista de modelos y un botón para eliminarlo,
 * y lo añade al contenedor especificado. Este sistema permite añadir múltiples modelos
 * de forma dinámica en el formulario.
 *
 * @param {Array<Object>} modelos - Lista de objetos de modelo con las propiedades:
 *   - {number|string} ID_MODELO - El identificador del modelo.
 *   - {string} NOMBRE_MODELO - El nombre del modelo a mostrar en el select.
 * @param {HTMLElement} contenedor - El elemento contenedor donde se insertará el select.
 */
function agregarSelect(modelos, contenedor) {
    let div = document.createElement('div'); // contenedor para el select y su botón
    div.className = 'modelo-select-div';

    let select = document.createElement('select');
    select.name = 'MODELO_FK';

    let defaultOption = document.createElement('option');
    defaultOption.disabled = true;
    defaultOption.selected = true;
    defaultOption.innerHTML = 'Seleccione un modelo';
    select.appendChild(defaultOption);

    modelos.forEach(modelo => {
        let option = document.createElement('option');
        option.value = modelo.ID_MODELO;
        option.innerHTML = modelo.NOMBRE_MODELO;
        select.appendChild(option);
    });

    // Botón eliminar
    let button_delete = document.createElement('button');
    button_delete.type = 'button';
    button_delete.className = 'btn-remove-modelo';
    button_delete.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="red" viewBox="0 0 16 16">
            <circle cx="8" cy="8" r="7" stroke="white" stroke-width="1.5" fill="red" />
            <line x1="4.5" y1="8" x2="11.5" y2="8" stroke="white" stroke-width="1.5"/>
        </svg>`;

    button_delete.addEventListener('click', () => {
        div.remove();
    });

    div.appendChild(select);
    div.appendChild(button_delete);
    contenedor.appendChild(div);
}


/**
 * Obtiene los datos que quiere agregar el usuario
 * @param {string} table contiene el nombre de la tabla
 * @returns Devuelve un objecto JSON con los datos del formulario
 */
function obtenerDatosAgregar(table) {
    // Seleccionamos todos los divs que coinciden con la clase correspondiente
    let divs = Array.from(document.querySelectorAll('div')).filter(div => div.classList.contains(`class-name-agregar`));
    let filaDatos = {};
    let modelosArray = [];
    let imagenesArray = [];
    divs.forEach(div => {
        // Seleccionar todos los span dentro del div
        let spans = div.querySelectorAll('span');

        spans.forEach(span => {
            let input = span.querySelector('input');
            let select = span.querySelector('select');

            if (input) {
                if (input.name === 'PRECIO_COMPRA' || input.name === 'PRECIO' || input.name === 'SUELDO_BRUTO' || input.name === 'PESO') {
                    let value = input.value.trim().replace(',', '.');

                    if (!value.includes('.')) {
                        // Si no tiene decimal, añadimos ".00"
                        value += '.00';
                    } else {
                        // Si tiene decimal, verificamos la parte decimal
                        let partes = value.split('.');
                        if (partes[1].length === 0) {
                            value += '00';
                        } else if (partes[1].length === 1) {
                            value += '0';
                        } else if (partes[1].length > 2) {
                            // Si tiene más de 2 decimales, recortamos a dos
                            value = partes[0] + '.' + partes[1].substring(0, 2);
                        }
                    }

                    input.value = value;
                }

                filaDatos[input.name] = input.value;

                if (input.name === 'MES') {
                    console.log('JODER')
                    let value = input.value;
                    filaDatos[input.name] = value + '-01';
                }

            }
            // Recoge los selects normales (excepto MODELO_FK)
            if (select && select.name !== 'MODELO_FK') {
                filaDatos[select.name] = select.value;
            }
        });

        // Aquí recogemos todos los selects MODELO_FK dentro del contenedor-modelos
        let modelosSelects = div.querySelectorAll('.contenedor-modelos select[name="MODELO_FK"]');
        modelosSelects.forEach(select => {
            modelosArray.push(select.value);
        });

        // Recoge todas las imágenes de los inputs file
        let imagenInputs = div.querySelectorAll('input[type="file"][name="IMAGENES"]');
        imagenInputs.forEach(input => {
            for (let i = 0; i < input.files.length; i++) {
                imagenesArray.push(input.files[i]);
            }
        })
    });

    if (table.toUpperCase().includes('VACACIONES')) {
        filaDatos['token'] = token;
    }

    if (modelosArray.length > 0) {
        filaDatos['MODELOS_FK'] = modelosArray;
    }

    // Si es la tabla PIEZAS, usamos FormData para enviar las imágenes
    if (table.toUpperCase().includes('PIEZAS')) {
        let formData = new FormData();

        // Añadimos los datos normales
        for (let key in filaDatos) {
            if (Array.isArray(filaDatos[key])) {
                filaDatos[key].forEach(value => formData.append(`${key}[]`, value));
            } else {
                formData.append(key, filaDatos[key]);
            }
        }

        // Añadimos las imágenes
        imagenesArray.forEach((file, index) => {
            formData.append('IMAGENES[]', file);
        });

        return formData;
    }

    console.log("Datos recogidos:", filaDatos);
    return filaDatos;
}

/**
 * Función que crea la tabla con los datos a mostrar
 * @param {Object} datos Contiene los datos a mostrar en un objecto JSON.
 * @param {string} tabla Contiene el nombre de la tabla a la que hacen referencia los datos.
 */
async function crearTablaConsulta(datos, tabla) {
    let div_table_container = document.getElementById('table-container');
    div_table_container.innerHTML = '';  // Limpiar el contenedor de la tabla

    let table = document.createElement('table');
    table.className = `class-name-consultar`;  // Asignar una clase a la tabla

    // Crear el encabezado de la tabla
    let table_head = document.createElement('thead');
    let tr = document.createElement('tr');
    let marcas, categorias, modelos, empleados, vendedores, clientes, facturas;
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

    if (tabla.toUpperCase().includes('FACTURAS')) {
        clientes = await obtenerDatosFK('CLIENTE');
        facturas = await obtenerDatosFK('FACTURAS');
    }

    if (tabla.toUpperCase().includes('NOMINAS')) {
        empleados = await obtenerDatosFK('EMPLEADOS');
    }

    console.log(`Los datos son ${JSON.stringify(datos[0])}`)
    // Iterar sobre las claves de los primeros datos para crear el encabezado
    Object.keys(datos[0]).forEach(key => {
        let th = document.createElement('th');
        if (key.includes('ID_PIEZAS_MODELOS')) { return }
        if ((key.includes('CLIENTE_FK') || (key.includes('PIEZAS_FK')) && tabla.toUpperCase() === 'FACTURAS')) { return }
        if ((key.includes('EMPLEADOS_FK') || (key.includes('DATOS_COMUNES_FK')) && tabla.toUpperCase() === 'NOMINAS')) { return }
        if (key !== 'DNI_CIF') {
            if (key === 'APELLIDO_B') {
                th.innerHTML = key.replace(/_B/g, '');
            }
            th.innerHTML = key.replace(/_A/g, '').replace(/_/g, ' ').replace('FK', '').replace('SS', 'SEG. SOCIAL').replace('FECHA YEAR', 'AÑO'); // Limpiar el nombre de las columnas
        } else {
            th.innerHTML = (tabla === 'EMPLEADOS' || (tabla === 'CLIENTE' || tabla === 'FACTURAS' || tabla === 'NOMINAS'
                || tabla === 'RESTABLECER_PASS')) ? 'DNI' : 'CIF';
        }

        tr.appendChild(th);
    });
    console.log(`Tipod e cuente: ${tipo_cuenta}`);
    if (!(["FICHAR", "NOMINAS"].includes(tabla) && tipo_cuenta === 'GESTOR')) {
        for (let i = 0; i < 2; i++) {
            let th = document.createElement('th');
            if (tabla === 'RESTABLECER_PASS') {
                th.innerHTML = i === 0 ? 'MODIFICAR' : 'NUEVA CONTRASEÑA';
            } else {
                th.innerHTML = i === 0 ? 'MODIFICAR' : 'ELIMINAR';
            }

            tr.appendChild(th);
        }
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
                key.includes('EMPLEADO_FK') || key.includes('MODELO_FK')) && tabla.toUpperCase() === 'PIEZAS' ||
                ((key.includes('CLIENTE_FK') || (key.includes('PIEZAS_FK')) && tabla.toUpperCase() === 'FACTURAS'))) {
                return;
            }

            if ((key.includes('EMPLEADOS_FK') || (key.includes('DATOS_COMUNES_FK')) && tabla.toUpperCase() === 'NOMINAS')) { return }

            let td = document.createElement('td');
            let elemento;

            elemento = document.createElement('input');
            elemento.value = fila[key];
            elemento.name = key;
            if (key.includes('FECHA') && (tabla.toUpperCase() === 'VACACIONES' || tabla.toUpperCase() === 'FACTURAS')) {
                elemento.type = 'date';

                // Obtener la fecha como cadena en formato 'YYYY-MM-DD' directamente desde la base de datos
                let fecha = new Date(fila[key]);

                // Extraer el año, mes y día de la fecha para evitar cualquier ajuste de zona horaria
                let anio = fecha.getFullYear();
                let mes = (fecha.getMonth() + 1).toString().padStart(2, '0');  // +1 porque los meses son 0-indexados
                let dia = fecha.getDate().toString().padStart(2, '0');

                // Crear la fecha en formato 'YYYY-MM-DD'
                let fechaFormateada = `${anio}-${mes}-${dia}`;

                // Asignar la fecha al input
                elemento.value = fechaFormateada;
            }
            elemento.disabled = true;

            if (key === 'CONCEDIDAS' || key === 'VENDIDO') {
                elemento = document.createElement('select');
                let option1 = document.createElement('option');
                option1.value = fila[key];
                option1.textContent = fila[key] === 0 ? 'NO' : 'SI';
                elemento.appendChild(option1);

                let option2 = document.createElement('option');
                option2.value = fila[key] === 0 ? 1 : 0;
                option2.textContent = fila[key] !== 0 ? 'NO' : 'SI';
                elemento.appendChild(option2);

                elemento.disabled = true;
                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

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
                    option.innerHTML = categoria.NOMBRE_CATEGORIA;
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

            if (key.includes('DNI_CIF') && tabla.toUpperCase() === 'FACTURAS') {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                let clienteActual = clientes.find(cliente => cliente.DNI_CIF === fila[key]);
                let idDefecto = clienteActual ? clienteActual.ID_CLIENTE : '';
                valorDefecto.value = idDefecto;
                valorDefecto.innerHTML = fila[key];
                elemento.name = 'CLIENTE_FK';
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

            if (key.includes('REFERENCIA') && tabla.toUpperCase() === 'FACTURAS') {
                elemento = document.createElement('select');
                // Agregar opción por defecto (marca de la fila actual)
                let valorDefecto = document.createElement('option');
                let referenciaActual = facturas.find(factura => factura.REFERENCIA === fila[key]);
                let idDefecto = referenciaActual ? referenciaActual.ID_PIEZAS : '';
                valorDefecto.value = idDefecto;
                elemento.name = 'PIEZAS_FK';
                valorDefecto.innerHTML = fila[key];
                elemento.appendChild(valorDefecto);
                facturas.forEach(factura => {
                    let option = document.createElement('option');
                    option.value = factura.ID_PIEZAS;
                    option.innerHTML = factura.REFERENCIA;
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

            if (key.includes('DNI_CIF') && tabla.toUpperCase() === 'NOMINAS') {
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

                // Guardar referencia al input del nombre
                let inputNombre = tr_body.querySelector('input');  // Suponiendo que ya se creó antes
                if (!inputNombre || !inputNombre.name || !inputNombre.name.includes('NOMBRE')) {
                    // Buscar input exacto del nombre si hay más de uno
                    inputNombre = [...tr_body.querySelectorAll('input')].find(input => input.name === 'NOMBRE');
                }

                // Evento para actualizar nombre al cambiar DNI
                elemento.addEventListener('change', () => {
                    let nuevoID = elemento.value;
                    let empleadoSeleccionado = empleados.find(emp => emp.ID_EMPLEADOS == nuevoID);
                    if (empleadoSeleccionado && inputNombre) {
                        inputNombre.value = empleadoSeleccionado.NOMBRE;
                    }
                });
                return;
            }

            if (key.includes('MES') && tabla.toUpperCase() === 'NOMINAS') {
                console.log("Valor del MES:", fila[key]);
                elemento = document.createElement('input');
                elemento.type = 'month';
                elemento.value = fila[key];
                elemento.name = key;
                elemento.disabled = true;
                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('ESTADO') && tabla.toUpperCase() === 'NOMINAS') {
                elemento = document.createElement('select');
                let estado = ['PENDIENTE', 'PAGADA', 'BAJA'];
                let estadoActual = fila[key];

                // Crear opción por defecto (estado actual)
                let valorDefecto = document.createElement('option');
                valorDefecto.value = estadoActual;
                valorDefecto.innerHTML = estadoActual;
                valorDefecto.selected = true;
                elemento.appendChild(valorDefecto);
                estado.forEach(stat => {
                    if (stat !== estadoActual) {
                        let option = document.createElement('option');
                        option.value = stat;
                        option.innerHTML = stat;
                        elemento.appendChild(option);
                    }
                });
                elemento.disabled = true;
                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            if (key.includes('HORA_ENTRADA') || key.includes('HORA_SALIDA') && tabla.toUpperCase() === 'FICHAR') {
                elemento = document.createElement('input');
                elemento.type = 'datetime-local';

                // Asegurarte que el valor esté en formato correcto para datetime-local
                if (fila[key]) {
                    let fechaHora = new Date(fila[key]);

                    // Formatearlo a 'YYYY-MM-DDTHH:MM' que es lo que espera datetime-local
                    let año = fechaHora.getFullYear();
                    let mes = (fechaHora.getMonth() + 1).toString().padStart(2, '0');
                    let dia = fechaHora.getDate().toString().padStart(2, '0');
                    let horas = fechaHora.getHours().toString().padStart(2, '0');
                    let minutos = fechaHora.getMinutes().toString().padStart(2, '0');

                    elemento.value = `${año}-${mes}-${dia}T${horas}:${minutos}`;
                }

                elemento.name = key;
                elemento.disabled = true;
                td.appendChild(elemento);
                tr_body.appendChild(td);
                elementosFila.push(elemento);
                return;
            }

            td.appendChild(elemento);
            tr_body.appendChild(td);
            elementosFila.push(elemento);
        });

        let idFila = Object.values(fila)[0];

        if (!(["FICHAR", "NOMINAS"].includes(tabla) && tipo_cuenta === 'GESTOR')) {


            if (tabla !== 'RESTABLECER_PASS') {
                // Crear los botones de modificar y eliminar
                let td_Mod = document.createElement('td');
                let button_Mod = document.createElement('button');
                button_Mod.innerHTML = '✏️';  // Ícono de lápiz para modificar
                button_Mod.addEventListener('click', async () => {
                    if (button_Mod.innerHTML === '✏️') {
                        button_Mod.innerHTML = '💾';  // Cambiar a icono de guardar
                        elementosFila.forEach((elemento, index) => {
                            if (index !== 0) {  // Evitar que el primer elemento se habilite
                                elemento.disabled = false;
                            }
                            if (elemento.name === 'BASE' || elemento.name === 'IVA' || elemento.name === 'BASE' || elemento.name === 'PRECIO_PIEZA'
                                && tabla.toUpperCase() === 'FACTURAS') {
                                elemento.disabled = true;
                            }

                            if (elemento.name === 'DNI_CIF' || elemento.name === 'FECHA_INICIO' || elemento.name === 'FECHA_FINAL' && tabla.toUpperCase() === 'VACACIONES') {
                                elemento.disabled = true;
                            }

                            if (elemento.name === 'DNI_CIF' && tabla.toUpperCase() === 'FICHAR') {
                                elemento.disabled = true;
                            }
                        });  // Habilitar inputs 
                    } else {
                        button_Mod.innerHTML = '✏️';  // Volver al lápiz
                        elementosFila.forEach(elemento => elemento.disabled = true);  // Deshabilitar edición

                        // Obtener nuevos valores del input y select
                        let nuevosDatos = elementosFila.map(el => el.value);
                        console.log(`Los datos son ${nuevosDatos}`);
                        let datosModificar = {};
                        // Agregar los datos modificados como un JSON
                        Object.keys(datos[0]).forEach((key, index) => {
                            console.log(`El valor es ${nuevosDatos[index]} la clave es: ${key}`);
                            if (key === 'NOMBRE_MARCA' && tabla.toUpperCase() === 'MODELOS') {
                                datosModificar['MARCA_MODELO_FK'] = nuevosDatos[index].trim();
                                return;
                            }
                            if (key === 'MES') {
                                datosModificar[key] = nuevosDatos[index] + '-01';
                                return;
                            }
                            if (key === 'EMPLEADOS_FK') {
                                datosModificar[key] = nuevosDatos[index + 1];
                                return;
                            }

                            if (tabla.toUpperCase() === 'FACTURAS' && key === 'DNI_CIF') {
                                datosModificar['PIEZAS_FK'] = nuevosDatos[index];
                                return
                            }
                            if (tabla.toUpperCase() === 'FACTURAS' && key === 'PIEZAS_FK') {
                                datosModificar['PRECIO_PIEZA'] = nuevosDatos[index];
                                return
                            }
                            if (tabla.toUpperCase() === 'FACTURAS' && key === 'FECHA') {
                                let fecha = nuevosDatos[index];
                                let formatFecha = fecha.substring(0, 10);
                                datosModificar['FECHA'] = formatFecha;
                                return
                            }
                            if (tabla.toUpperCase() === 'FACTURAS' && (key === 'REFERENCIA' || key === 'PRECIO_PIEZA')) { return }
                            if (tabla.toUpperCase() === 'FICHAR' && key === 'DNI_CIF') { return }

                            datosModificar[key] = nuevosDatos[index];
                        });
                        if (tabla.toUpperCase() === 'VACACIONES') {
                            let fechaInicio = new Date(datosModificar['FECHA_INICIO']);
                            let fechaFinal = new Date(datosModificar['FECHA_FINAL']);
                            if (fechaInicio >= fechaFinal) {
                                popUpError(`La fecha final: ${datosModificar['FECHA_FINAL']}, tiene que ser superior a la fecha de inicio: ${datosModificar['FECHA_INICIO']}.`)
                                return;
                            }
                        }
                        let resultado = await actualizarDatos(datosModificar, tabla);
                        popUpError(`${resultado.mensaje ? resultado.mensaje : resultado.error}`);
                        console.log(`Guardando cambios para ID: ${idFila}`, JSON.stringify(datosModificar));
                    }
                });

                td_Mod.appendChild(button_Mod);
                tr_body.appendChild(td_Mod);

                let td_Del = document.createElement('td');
                let button_Del = document.createElement('button');
                button_Del.innerHTML = '❌';  // Ícono de eliminar
                button_Del.addEventListener('click', async () => {
                    if (menuVerificarBorrado(idFila, tabla)) {
                        let key = Object.keys(fila)[0];
                        let datosEliminar = [{ [key]: idFila }];
                        console.log(`Eliminar registro con ID: ${idFila}`);
                        let resultado = await eliminarDatos(datosEliminar, tabla);
                        popUpError(`${resultado.mensaje ? resultado.mensaje : resultado.error}`);
                    }
                    return;
                });
                td_Del.appendChild(button_Del);
                tr_body.appendChild(td_Del);
            } else {
                let td_Mod = document.createElement('td');
                let button_Mod = document.createElement('button');
                let input_pass = document.createElement('input');
                button_Mod.innerHTML = '✏️';  // Ícono de lápiz para modificar
                button_Mod.addEventListener('click', async () => {
                    if (button_Mod.innerHTML === '✏️') {
                        button_Mod.innerHTML = '💾';  // Cambiar a icono de guardar
                        input_pass.disabled = false;
                    } else {
                        button_Mod.innerHTML = '✏️';  // Volver al lápiz
                        input_pass.disabled = true;

                        // Obtener nuevos valores del input y select
                        let nuevosDatos = elementosFila.map(el => el.value);
                        console.log(`Los datos son ${nuevosDatos}`);
                        let datosModificar = {};
                        datosModificar['ID_DATOS_COMUNES'] = idFila;
                        let value = input_pass.value;
                        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_])(?!.*\s).{8,20}$/;;
                        const esValida = regex.test(value);
                        if (!esValida) {
                            popUpError(`❌ La contraseña: ${value}. Tiene que tener una longitud entre 8 y 20 caracteres.` +
                                `Contener números, letras minúsculas y mayúsculas.` +
                                `Y al menos uno de los siguientes caracteres:` +
                                `¡ " # $ % & ' ( ) * + , - . / : ; < = > ? @ [ \\ ] ^ \\\` { | } ~ _`);
                            return;
                        }
                        datosModificar[tabla] = input_pass.value;


                        let resultado = await actualizarDatos(datosModificar, tabla);
                        popUpError(`${resultado.mensaje ? resultado.mensaje : resultado.error}`);
                        console.log(`Guardando cambios para ID: ${idFila}`, JSON.stringify(datosModificar));
                    }
                });
                td_Mod.appendChild(button_Mod);
                tr_body.appendChild(td_Mod);

                let td_pass = document.createElement('td');

                input_pass.placeholder = 'Nueva Contraseña';
                input_pass.name = 'RESTABLECER_PASS';
                input_pass.disabled = true;

                td_pass.appendChild(input_pass);
                tr_body.appendChild(td_pass);
            }
        }

        table_body.appendChild(tr_body);
    });

    table.appendChild(table_body);
    div_table_container.appendChild(table);  // Agregar la tabla al contenedor
}

/**
 * Devuelve los datos de una fila
 * @param {Array<Object>} datos Contiene los datos de la tabla
 * @param {int} idFila Contiene el número de la fila 
 * @returns Devuelve los datos de una fila
 */
function obtenerDatosFila(datos, idFila) {
    let resultado = {};

    resultado = datos[idFila];
    return resultado;
}

/**
 * Función que muestra el menú para seleccionar la contraseña de empleados o clientes.
 * @param {string} accion Contiene la acción para ejecutar en el backend
 */
async function modificarPassword() {
    let div_table_container = document.getElementById('table-container');
    div_table_container.innerHTML = '';

    let div_menu_container = document.getElementById('menu-container');
    div_menu_container.innerHTML = '';

    menuModPassword(div_table_container, div_menu_container);
}

/**
 * Crea el menú para buscar el usuario para cambiar la contraseña.
 * @param {div} div_table_container - Coontiene el div de la tabla
 * @param {div} div_menu_container  - Contiene el div del menú.
 */
async function menuModPassword(div_table_container, div_menu_container) {
    div_table_container.innerHTML = '';
    div_menu_container.innerHTML = '';
    let div;
    const COLUMNAS = await obtenerNombresColumnas('PASSWORD');
    console.log(COLUMNAS)

    div = document.createElement('div');
    div.className = `class-name-agregar-restablecer-pass`;

    let span = document.createElement('span');
    let p = document.createElement('p');
    p.textContent = 'Seleccione un campo de búsqueda';

    let select = document.createElement('select');
    select.name = 'buscar';
    let option = document.createElement('option');
    option.innerHTML = 'Selecciona una opción';
    select.appendChild(option);
    COLUMNAS.forEach(col => {
        option = document.createElement('option');
        option.innerHTML = col.COLUMN_NAME.replace(/_A/g, '').replace(/_B/g, '').replace('_', ' ');
        if (col.COLUMN_NAME === 'DNI_CIF') {
            option.innerHTML = 'DNI';
        }
        option.value = col.COLUMN_NAME;
        select.appendChild(option);
    });

    let input = document.createElement('input');
    select.addEventListener('change', () => {
        //input = document.createElement('input');
        input.name = select.value;
        input.placeholder = select.value.replace(/_A/g, '').replace(/_B/g, '').replace('_', ' ');
        if (select.value === 'DNI_CIF') {
            input.placeholder = 'DNI';
        }
    });

    let button_buscar = document.createElement('button');
    button_buscar.textContent = 'BUSCAR';
    button_buscar.addEventListener('click', async () => {
        console.log(`LLamar a buscar lalallalalaal ${input.value} valor a buscar ${input.name}`);
        let datos = {};
        datos[input.name] = input.value;
        let resultado_busqueda = await consultarDatos('RESTABLECER_PASS', datos);
        console.log(JSON.stringify(resultado_busqueda));
        crearTablaConsulta(resultado_busqueda, 'RESTABLECER_PASS');
    });

    span.appendChild(p);
    span.appendChild(select);
    span.appendChild(input);
    span.appendChild(button_buscar);
    div.append(span);

    div_menu_container.append(div);

}

/**
 * Menú para verificar si se quiere eliminar un registro.
 * @param {int} id - Contiene el número del id del registro a eliminar.
 * @param {*} tabla - Contiene el nombre de la tabla sobre la que se va eliminar el registro.
 */
function menuVerificarBorrado(id, tabla) {
    // Mostrar el fondo negro
    let overlay = document.getElementById('overlay');
    overlay.classList.remove('hidden');

    // Crear el menú 
    let menu = document.getElementById('error_menu_container');
    menu.classList.remove('hidden');
    menu.innerHTML = ''; // Limpiar contenido existente

    // Crear el botón de cerrar (X)
    let close_button = document.createElement('span');
    close_button.className = 'close_button';
    close_button.innerText = '✖';
    close_button.addEventListener('click', function () {
        overlay.classList.add('hidden');
        menu.classList.add('hidden');
    });
    menu.appendChild(close_button);

    // Crear div que contendrá el menu donde se muestra el el aviso
    let div = document.createElement('div');
    div.id = 'error_menu';
    let p = document.createElement('p');
    p.textContent = `Está seguro de que desea eliminar el registro con id: ${id} de la tabla: ${tabla}.`;

    for (let i = 0; i < 2; i++) {
        let button = document.createElement('button');
        button.innerHTML = i === 0 ? '✅ Borrar' : '❌ Cancelar';
        button.className = 'button_eliminar';
        button.addEventListener('click', () => {
            return i === 0 ? true : false;
        });
        div.appendChild(button);
    }
    div.appendChild(p);
    menu.appendChild(div);
}

/**
 * Función que crea la interfaz para que el usuario pueda fichar
 */
async function crearFormularioFichar() {
    let datos = await obtenerDatosFichaje();
    let div = document.getElementById('table-container');
    div.innerHTML = '';

    let button = document.createElement('button');

    if (datos.length > 0) {
        // Hay fichaje sin salida => mostrar para fichar salida
        let p = document.createElement('p');
        p.textContent = 'Fichaje de entrada: ' + datos[0].HORA_ENTRADA;
        p.className = `class-p-fichaje`;
        div.appendChild(p);

        button.textContent = 'Fichar salida';
        button.id = 'fichar_salida';
        button.className = 'class-button-agregar';
        button.addEventListener('click', function () {
            agregarFichaje(true, datos[0].ID_FICHAJE);
        });
    } else {
        // No hay fichaje hoy => mostrar para fichar entrada
        button.textContent = 'Fichar entrada';
        button.id = 'fichar_entrada';
        button.className = 'class-button-agregar';
        button.addEventListener('click', function () {
            agregarFichaje(false);
        });
    }

    div.appendChild(button);
}

