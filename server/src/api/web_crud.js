import { realizarConsulta } from "../../mysql/consultas_mysql.js";
import { app } from "../../desguace.js";
import { logger } from "../log/log.js";
import { registrarUsuario, eliminarUsuario } from "../sockets/auth/reg_user.js";

const tablas = ['CATEGORIAS', 'DATOS_COMUNES', 'VENDEDOR', 'VENDEDORES', 'CLIENTE', 'EMPLEADOS', 'NOMINAS',
    'FICHAJE', 'VACACIONES', 'MARCAS', 'MODELOS', 'FACTURA', 'PIEZAS', 'PIEZAS_MODELOS', 'LINEA_FACTURA', 'MODELO'
];

app.post('/ObtenerNombreColumnas', (req, res) => {
    console.log("✅ Ruta /ObtenerNombreColumnas ha sido llamada");
    let tabla = req.body.tabla;

    if (!tabla || !verificarTabla(tabla)) {
        return res.status(400).json({ error: "Tabla no encontrada" });
    }
    tabla = tabla.toUpperCase();
    let sql = '';
    switch (tabla) {
        case 'EMPLEADOS':
            sql = 'CALL ObtenerNombresColumnasEmpleados();';
            break;
        case 'VENDEDORES':
            sql = 'CALL ObtenerNombresColumnasVendedores();';
            break;
        case 'MODELOS':
            sql = 'CALL ObtenerNombresColumnasModelos();';
            break;
        case 'MARCAS':
            sql = 'CALL ObtenerNombresColumnasMarcas();';
            break;
        case 'PIEZAS':
            sql = 'CALL ObtenerNombresColumnasPiezas();';
            break;
    }
    logger.debug(`/src/api/web_crud.js POST: ObtenerNombreColumnas || tabla consultada: ${tabla}`);
    realizarConsulta(sql)
        .then(resultado => {
            logger.debug(`/src/api/web_crud.js POST: ObtenerNombreColumnas || Resultado de la consulta de creación de la tabla ${tabla} ` + JSON.stringify(resultado[0]));
            console.log(`✅ Obtenidas las columnas correctamente para la tabla ${tabla}`);
            res.json(resultado[0]);
        })
        .catch(error => {
            console.error(`❌ Error al obtener los datos de las columnas de la tabla ${tabla} error: ${error}.`);
            res.status(500).json({ error: "Error en la consulta SQL" });
        });
});

app.post('/ObtenerDatosClaveForanea', (req, res) => {
    console.log("✅ Ruta /ObtenerDatosClaveForanea ha sido llamada");
    let tabla = req.body.tabla;

    if (!tabla || !verificarTabla(tabla)) {
        return res.status(400).json({ error: "Tabla no encontrada." });
    }
    tabla = tabla.toUpperCase();
    let sql;
    switch (tabla) {
        case 'VENDEDOR':
            sql = `SELECT v.ID_VENDEDOR, dc.RAZON_SOCIAL, dc.DNI_CIF, dc.MAIL, dc.DIRECCION, dc.CODIGO_POSTAL,
            dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.NUMERO_CUENTA
            FROM VENDEDOR v 
            JOIN DATOS_COMUNES dc ON v.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'CLIENTE':
            sql = `SELECT c.ID_CLIENTE, dc.NOMBRE, dc.DNI_CIF 
            FROM CLIENTE c
            JOIN DATOS_COMUNES dc ON c.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'EMPLEADOS':
            sql = `SELECT e.ID_EMPLEADOS, dc.NOMBRE, dc.DNI_CIF
            FROM EMPLEADOS e
            JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'MARCAS':
            sql = `SELECT ID_MARCAS, NOMBRE_MARCA FROM MARCAS`;
            break;
        case 'CATEGORIAS':
            sql = `SELECT ID_CATEGORIAS_PIEZAS, NOMBRE FROM CATEGORIAS_PIEZAS`;
            break;
        case 'MODELOS':
            sql = `SELECT ID_MODELO, NOMBRE_MODELO FROM MODELO`;
            break;
        default:
            sql = `SELECT * FROM ?`;
            break;
    }
    logger.debug(`/src/api/web_crud.js POST: ObtenerDatosClaveForanea || tabla consultada: ${tabla}`);
    realizarConsulta(sql, tabla)
        .then(resultado => {
            logger.debug(`/src/api/web_crud.js POST: ObtenerDatosClaveForanea || Resultado de la consulta de creación de la tabla ${tabla} ` + JSON.stringify(resultado));
            console.log(`✅ Datos clave foranea obtenidos correctamente para la tabla ${tabla}`);
            res.json(resultado);
        })
        .catch(error => {
            console.error(`❌ Error al obtener datos de las claves foraneas: ${error}`);
            res.status(500).json({ error: "Error en la consulta SQL" });
        });
});

/**
 * Obtener datos para mostrar de las tablas
 */
app.post('/ObtenerDatosTablas', (req, res) => {
    console.log("✅ Ruta /ObtenerDatosTablas ha sido llamada");
    let tabla = req.body.tabla;

    if (!tabla || !verificarTabla(tabla)) {
        return res.status(400).json({ error: "Tabla no encontrada." });
    }
    tabla = tabla.toUpperCase();
    let sql;
    switch (tabla) {
        case 'VENDEDORES':
            sql = `SELECT v.ID_VENDEDOR, dc.RAZON_SOCIAL, dc.DNI_CIF, dc.MAIL, dc.DIRECCION, dc.CODIGO_POSTAL,
            dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.NUMERO_CUENTA
            FROM VENDEDOR v 
            JOIN DATOS_COMUNES dc ON v.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'CLIENTE':
            sql = `SELECT c.ID_CLIENTE, dc.NOMBRE, dc.APELLIDO_A, dc.APELLIDO_B, dc.DIRECCION, dc.CODIGO_POSTAL, 
            dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.MAIL, dc.DNI_CIF, , dc.NUMERO_CUENTA, c.DATOS_TARJETA 
            FROM CLIENTE c
            JOIN DATOS_COMUNES dc ON c.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'EMPLEADOS':
            sql = `SELECT e.ID_EMPLEADOS, dc.NOMBRE, dc.APELLIDO_A, dc.APELLIDO_B, dc.DIRECCION, dc.CODIGO_POSTAL, 
            dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.MAIL, dc.DNI_CIF, dc.NUMERO_CUENTA, e.NUMERO_SS, e.TIPO_CUENTA, e.TOTAL_VACACIONES
            FROM EMPLEADOS e 
            JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'NOMINAS':
            sql = `
            SELECT n.SUELDO_BRUTO, n.ESTADO, DATE_FORMAT(n.MES, '%Y-%m') AS MES, n.EMPLEADOS_FK, e.DATOS_COMUNES_FK, dc.NOMBRE, 
            dc.APELLIDO_A, dc.APELLIDO_B, dc.DNI_CIF
            FROM NOMINAS n
            JOIN EMPLEADOS e ON n.EMPLEADOS_FK = e.ID_EMPLEADOS
            JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'FICHAR':
            sql = ``;
            break;
        case 'VACACIONES':
            sql = ``;
            break;
        case 'MARCAS':
            sql = `SELECT ID_MARCAS, NOMBRE_MARCA FROM MARCAS`;
            break;
        case 'MODELOS':
            tabla = `MODELO`;
            sql = `SELECT m.ID_MODELO, m.NOMBRE_MODELO, mar.NOMBRE_MARCA 
            FROM MODELO m
            JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS`;
            break;
        case 'PIEZAS':
            sql = `SELECT p.ID_PIEZAS, p.DESCRIPCION, p.PESO, p.PRECIO_COMPRA, p.PRECIO, p.REFERENCIA, p.FECHA_YEAR, p.FECHA_INICIO_VENTA, p.IMAGENES, 
            c.NOMBRE AS CATEGORIAS, dc_vendedor.RAZON_SOCIAL, dc_empleado.DNI_CIF AS EMPLEADO, 
            p.VENDIDO, m.NOMBRE_MODELO, mar.NOMBRE_MARCA, dc_cliente.DNI_CIF AS CLIENTE, pm.ID_PIEZAS_MODELOS  
            FROM PIEZAS p
            LEFT JOIN CATEGORIAS_PIEZAS c ON p.CATEGORIAS_FK = c.ID_CATEGORIAS_PIEZASa
            LEFT JOIN VENDEDOR v ON p.VENDEDOR_FK = v.ID_VENDEDOR
            LEFT JOIN DATOS_COMUNES dc_vendedor ON v.DATOS_COMUNES_FK = dc_vendedor.ID_DATOS_COMUNES
            LEFT JOIN EMPLEADOS e ON p.EMPLEADO_FK = e.ID_EMPLEADOS
            LEFT JOIN DATOS_COMUNES dc_empleado ON e.DATOS_COMUNES_FK = dc_empleado.ID_DATOS_COMUNES
            LEFT JOIN PIEZAS_MODELOS pm ON p.ID_PIEZAS = pm.PIEZAS_FK
            LEFT JOIN MODELO m ON pm.MODELO_FK = m.ID_MODELO
            LEFT JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS 
            LEFT JOIN LINEA_FACTURA lf ON p.ID_PIEZAS = lf.PIEZAS_FK
            LEFT JOIN FACTURA f ON lf.FACTURA_FK = f.ID_FACTURAS
            LEFT JOIN CLIENTE cli ON f.CLIENTE_FK = cli.ID_CLIENTE
            LEFT JOIN DATOS_COMUNES dc_cliente ON cli.DATOS_COMUNES_FK = dc_cliente.ID_DATOS_COMUNES`;
            /* SELECT p.ID_PIEZAS, p.DESCRIPCION, p.PESO, p.PRECIO_COMPRA, p.PRECIO, p.REFERENCIA, p.FECHA_YEAR, p.FECHA_INICIO_VENTA, p.IMAGENES, 
            p.CATEGORIAS_FK, c.NOMBRE AS CATEGORIAS, p.VENDEDOR_FK, dc_vendedor.RAZON_SOCIAL, p.EMPLEADO_FK, dc_empleado.DNI_CIF AS EMPLEADO, 
            p.VENDIDO, m.NOMBRE_MODELO, pm.MODELO_FK, mar.NOMBRE_MARCA 
            FROM PIEZAS p
            LEFT JOIN CATEGORIAS_PIEZAS c ON p.CATEGORIAS_FK = c.ID_CATEGORIAS_PIEZAS
            LEFT JOIN VENDEDOR v ON p.VENDEDOR_FK = v.ID_VENDEDOR
            LEFT JOIN DATOS_COMUNES dc_vendedor ON v.DATOS_COMUNES_FK = dc_vendedor.ID_DATOS_COMUNES
            LEFT JOIN EMPLEADOS e ON p.EMPLEADO_FK = e.ID_EMPLEADOS
            LEFT JOIN DATOS_COMUNES dc_empleado ON e.DATOS_COMUNES_FK = dc_empleado.ID_DATOS_COMUNES
            LEFT JOIN PIEZAS_MODELOS pm ON p.ID_PIEZAS = pm.PIEZAS_FK
            LEFT JOIN MODELO m ON pm.MODELO_FK = m.ID_MODELO
            LEFT JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS */
            break;
        case 'CATEGORIAS':
            sql = `SELECT ID_CATEGORIAS_PIEZAS, NOMBRE FROM CATEGORIAS_PIEZAS`;
            break;
        case 'VACACIONES': {
            break;
        }
        case 'FACTURAS': {
            break;
        }
        default:
            sql = `SELECT * FROM ?`;
            break;
    }
    logger.debug(`/src/api/web_crud.js POST: ObtenerDatosTablas || tabla consultada: ${tabla}`);
    realizarConsulta(sql, tabla)
        .then(resultado => {
            logger.debug(`/src/api/web_crud.js POST: ObtenerDatosTablas || Resultado de la consulta de creación de la tabla ${tabla} ` + JSON.stringify(resultado));
            console.log(`✅ Datos para la tabla ${tabla} obtenidos correctamente.`);
            res.json(resultado);
        })
        .catch(error => {
            console.error(`❌ Error al obtener datos de la tabla ${tabla}: ${error}`);
            res.status(500).json({ error: "Error en la consulta SQL" });
        });
});

/**
 * Agregar datos a una tabla
 */
app.put('/AgregarDatosTabla', async (req, res) => {
    console.log("✅Ruta /AgregarDatosTabla ha sido llamada");

    let { datos, tabla } = req.body;
    if (!datos || !tabla) {
        return res.status(400).json({ error: "Datos incompletos." });
    }
    if (!verificarTabla(tabla)) {
        return res.status(400).json({ error: "Tabla incorrecta." });
    }
    tabla = tabla.toUpperCase();

    switch (tabla) {
        case "EMPLEADOS":
            await agregarEmpleado(datos, res);
            break;
        case "VENDEDORES":
            await agregarVendedores(datos, res);
            break;
        case "MODELOS":
            await agregarModelos(datos, res);
            break;
        case "MARCAS":
            await agregarMarcas(datos, res);
            break;
        default:
            return res.status(400).json({ error: "Tabla no soportada." });
    }
});

app.put('/ModificarDatosTabla', async (req, res) => {
    console.log("✅Ruta /ModificarDatosTabla ha sido llamada");

    let { datos, tabla } = req.body;
    if (!datos || !tabla) {
        return res.status(400).json({ error: "Datos incompletos." });
    }
    if (!verificarTabla(tabla)) {
        return res.status(400).json({ error: "Tabla incorrecta." });
    }
    tabla = tabla.toUpperCase();

    switch (tabla) {
        case "EMPLEADOS":
            await modificarDatos(datos, res, tabla);
            break;
        case "VENDEDORES":
            await modificarDatos(datos, res, 'VENDEDOR');
            break;
        case "MODELOS":
            await modificarDatos(datos, res, 'MODELO');
            break;
        case "MARCAS":
            await modificarDatos(datos, res, 'MARCAS');
            break;
        case "PIEZAS":
            await modificarDatos(datos, res, 'PIEZAS');
            break;
        default:
            return res.status(400).json({ error: "Tabla no soportada." });
    }
});

/**
 * Utilizando el método some() se verifica si la tabla a consultar existe.
 * @param {String} tabla Contiene el nombre de la tabla.
 * @returns Devuelve true si hay un coincidencia.
 */
function verificarTabla(tabla) {
    return tablas.some(table => table === tabla.toUpperCase());
}

/**
 * Agrega un empleado en la base de datos.
 * @param {Object} datos - Objeto que contiene la información del empleado a agregar.
 * @param {Object} res - Objeto de respuesta HTTP para enviar la respuesta al cliente. 
 * @returns {Promise<void>} Retorna una respuesta JSON con el resultado de la operación.
 */
async function agregarEmpleado(datos, res) {
    try {
        // Crear usuario en FIREBASE
        const FIREBASE_UID = await registrarUsuario(datos.MAIL, datos.CONTRASENA);

        // Validar si se insertó correctamente en FIREBASE
        if (!FIREBASE_UID) {
            console.error("❌ Error src/api/web_crud.js al registrar usuario en Firebase.");
            return res.status(500).json({ error: `Error al agregar Empleado ${datos.NOMBRE}` });
        }

        // Insertar DATOS_COMUNES
        let sqlDatosComunes = `INSERT INTO DATOS_COMUNES 
                           (NOMBRE, APELLIDO_A, APELLIDO_B, CONTRASENA, DIRECCION, 
                            CODIGO_POSTAL, CIUDAD, MUNICIPIO, TELEFONO, MOVIL, MAIL, 
                            DNI_CIF, FIREBASE_UID, RAZON_SOCIAL, NUMERO_CUENTA) 
                           VALUES (?, ?, ?, SHA2(?, 256), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`;

        let valoresDatosComunes = [
            datos.NOMBRE, datos.APELLIDO_A, datos.APELLIDO_B, datos.CONTRASENA, datos.DIRECCION,
            datos.CODIGO_POSTAL, datos.CIUDAD, datos.MUNICIPIO, datos.TELEFONO, datos.MOVIL,
            datos.MAIL, datos.DNI_CIF, FIREBASE_UID, null, datos.NUMERO_CUENTA
        ];

        let resultadoDatosComunes = await realizarConsulta(sqlDatosComunes, valoresDatosComunes);

        // Validar si se insertó correctamente en DATOS_COMUNES
        if (!resultadoDatosComunes || !resultadoDatosComunes.insertId) {
            console.error("❌ Error src/api/web_crud.js al insertar en DATOS_COMUNES.");
            // Borrar registros
            await eliminarUsuario(FIREBASE_UID);
            return res.status(500).json({ error: `Error al agregar Empleado ${datos.NOMBRE}` });
        }

        let idDatosComunes = resultadoDatosComunes.insertId; // Obtener el ID generado

        // Insertar datos en la tabla empleados con el ID obtenido de los DATOS_COMUNES
        let sqlEmpleados = `INSERT INTO EMPLEADOS 
                            (NUMERO_SS, TIPO_CUENTA, TOTAL_VACACIONES, DATOS_COMUNES_FK) 
                            VALUES (?, ?, ?, ?)`;

        let valoresEmpleados = [
            datos.NUMERO_SS, datos.TIPO_CUENTA, datos.TOTAL_VACACIONES, idDatosComunes
        ];

        let resultadoEmpleados = await realizarConsulta(sqlEmpleados, valoresEmpleados);

        // Verificar si se insertó correctamente en EMPLEADOS
        if (!resultadoEmpleados || resultadoEmpleados.affectedRows === 0) {
            console.error(`❌ Error src/api/web_crud.js al insertar en EMPLEADOS: ${error}`);
            const sqlBorrar = `DELETE FROM DATOS_COMUNES WHERE ID_DATOS_COMUNES = ?`;
            // Borrar registros
            await realizarConsulta(sqlBorrar, [idDatosComunes]);
            await eliminarUsuario(FIREBASE_UID);
            return res.status(500).json({ error: `Error al agregar Empleado ${datos.NOMBRE}` });
        }

        console.log(`✅ Datos agregados correctamente en src/api/web_crud.js en la tabla EMPLEADOS con ID_DATOS_COMUNES: ${idDatosComunes}`);
        return res.status(200).json({ mensaje: `El empleado ${datos.NOMBRE} insertado correctamente en la base de datos.` });

    } catch (error) {
        console.error(`❌ Error src/api/web_crud.js al insertar en EMPLEADOS: ${error}`);
        return res.status(500).json({ error: `Error al agregar Empleado ${datos.NOMBRE}` });
    }
}

/**
 * Agrega un vendedor en la base de datos.
 * @param {Object} datos - Objeto que contiene la información del verdedor a agregar.
 * @param {Object} res - Objeto de respuesta HTTP para enviar la respuesta al cliente. 
 * @returns {Promise<void>} Retorna una respuesta JSON con el resultado de la operación.
 */
async function agregarVendedores(datos, res) {
    try {
        let sqlDatosComunes = `INSERT INTO DATOS_COMUNES (DIRECCION, CODIGO_POSTAL, CIUDAD, MUNICIPIO, TELEFONO, MOVIL, 
        MAIL, DNI_CIF, RAZON_SOCIAL, NUMERO_CUENTA) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`;

        let valoresDatosComunes = [
            datos.DIRECCION, datos.CODIGO_POSTAL, datos.CIUDAD, datos.MUNICIPIO, datos.TELEFONO, datos.MOVIL,
            datos.MAIL, datos.DNI_CIF, datos.RAZON_SOCIAL, datos.NUMERO_CUENTA
        ];

        let resultadoDatosComunes = await realizarConsulta(sqlDatosComunes, valoresDatosComunes);

        // Validar si se insertó correctamente en DATOS_COMUNES
        if (!resultadoDatosComunes || !resultadoDatosComunes.insertId) {
            console.error("❌ Error src/api/web_crud.js al insertar en DATOS_COMUNES.");
            return res.status(500).json({ error: `Error al agregar Vendedor ${datos.RAZON_SOCIAL}` });
        }

        let idDatosComunes = resultadoDatosComunes.insertId; // Obtener el ID generado

        // Insertar datos en la tabla vendedor con el ID obtenido de los DATOS_COMUNES
        let sqlVendedor = `INSERT INTO VENDEDOR (DATOS_COMUNES_FK) VALUES (?)`;

        let valoresVendedor = [idDatosComunes];

        let resultadoVendedor = await realizarConsulta(sqlVendedor, valoresVendedor);

        // Verificar si se insertó correctamente en EMPLEADOS
        if (!resultadoVendedor || resultadoVendedor.affectedRows === 0) {
            console.error(`❌ Error src/api/web_crud.js al insertar en Vendedor: ${error}`);
            const sqlBorrar = `DELETE FROM DATOS_COMUNES WHERE ID_DATOS_COMUNES = ?`;
            // Borrar registros
            await realizarConsulta(sqlBorrar, [idDatosComunes]);
            return res.status(500).json({ error: `Error al agregar Empleado ${datos.NOMBRE}` });
        }

        console.log(`✅ Datos agregados correctamente en src/api/web_crud.js en la tabla Vendedor con ID_DATOS_COMUNES: ${idDatosComunes}`);
        return res.status(200).json({ mensaje: `El vendedor ${datos.RAZON_SOCIAL} insertado correctamente en la base de datos.` });
    } catch (error) {
        console.error(`❌ Error src/api/web_crud.js al insertar en Vendedor: ${error}`);
        return res.status(500).json({ error: `Error al agregar Vendedor ${datos.RAZON_SOCIAL}` });
    }
}

/**
 * Agrega un modelo en la base de datos.
 * @param {Object} datos - Objeto que contiene la información del modelo a agregar.
 * @param {Object} res - Objeto de respuesta HTTP para enviar la respuesta al cliente. 
 * @returns {Promise<void>} Retorna una respuesta JSON con el resultado de la operación.
 */
async function agregarModelos(datos, res) {
    try {
        let sql = `INSERT INTO MODELO (NOMBRE_MODELO, MARCA_MODELO_FK) VALUES (?, ?)`;

        let valores = [datos.NOMBRE_MODELO, datos.NOMBRE_MARCA];

        let resultado = await realizarConsulta(sql, valores);

        // Validar si se insertó correctamente en Modelo
        if (!resultado || !resultado.insertId) {
            console.error("❌ Error src/api/web_crud.js al insertar en MODELO.");
            return res.status(500).json({ error: `Error al agregar Modelo ${datos.NOMBRE_MODELO}` });
        }

        console.log(`✅ Datos agregados correctamente en src/api/web_crud.js en la tabla MODELO con: ${datos.NOMBRE_MODELO}`);
        return res.status(200).json({ mensaje: `El modelo ${datos.NOMBRE_MODELO} insertado correctamente en la base de datos.` });

    } catch (error) {
        console.error(`❌ Error src/api/web_crud.js al insertar en Modelos: ${error}`);
        return res.status(500).json({ error: `Error al agregar Modelos ${datos.NOMBRE_MODELO}` });
    }
}

/**
 * Agrega un modelo en la base de datos.
 * @param {Object} datos - Objeto que contiene la información del modelo a agregar.
 * @param {Object} res - Objeto de respuesta HTTP para enviar la respuesta al cliente. 
 * @returns {Promise<void>} Retorna una respuesta JSON con el resultado de la operación.
 */
async function agregarMarcas(datos, res) {
    try {
        let sql = `INSERT INTO MARCAS (NOMBRE_MARCA) VALUES (?)`;

        let valores = [datos.NOMBRE_MARCA];

        let resultado = await realizarConsulta(sql, valores);

        // Validar si se insertó correctamente en Modelo
        if (!resultado || !resultado.insertId) {
            console.error("❌ Error src/api/web_crud.js al insertar en Marcas.");
            return res.status(500).json({ error: `Error al agregar Marcas ${datos.NOMBRE_MARCA}` });
        }

        console.log(`✅ Datos agregados correctamente en src/api/web_crud.js en la tabla Marcas con: ${datos.NOMBRE_MARCA}`);
        return res.status(200).json({ mensaje: `La marca ${datos.NOMBRE_MARCA} insertado correctamente en la base de datos.` });

    } catch (error) {
        console.error(`❌ Error src/api/web_crud.js al insertar en Marcas: ${error}`);
        return res.status(500).json({ error: `Error al agregar Marcas ${datos.NOMBRE_MARCA}` });
    }
}

async function modificarDatos(datos, res, tabla) {
    try {
        console.log(`Datos son: ${JSON.stringify(datos)}`)
        const id = Object.values(datos)[0];
        console.log(`El ID  ${id} a consultar en la tabla ${tabla}`)

        //tabla = tabla.toUpperCase();

        if (!verificarTabla(tabla)) {
            return res.status(404).json({ error: "Tabla de consulta errónea." });
        }

        // Obtener los datos actuales de la tabla a consultar
        let sqlConsultaDatos = '';
        console.log(`tabla seleccionada ${tabla}, los datos ${JSON.stringify(datos)}`);
        switch (tabla) {
            case 'EMPLEADOS': {
                sqlConsultaDatos = `
               SELECT e.DATOS_COMUNES_FK, dc.NOMBRE, dc.APELLIDO_A, dc.APELLIDO_B, dc.DIRECCION, 
               dc.CODIGO_POSTAL, dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.MAIL, 
               dc.DNI_CIF, dc.NUMERO_CUENTA, e.NUMERO_SS, e.TIPO_CUENTA, e.TOTAL_VACACIONES 
               FROM EMPLEADOS e 
               JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES 
               WHERE e.ID_EMPLEADOS = ?;`;
                break;
            }
            case 'VENDEDOR': {
                sqlConsultaDatos = `
                SELECT v.DATOS_COMUNES_FK, dc.RAZON_SOCIAL, dc.DIRECCION, dc.CODIGO_POSTAL, dc.CIUDAD,
                dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.MAIL, dc.DNI_CIF, dc.NUMERO_CUENTA 
                FROM VENDEDOR v 
                JOIN DATOS_COMUNES dc ON v.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES 
                WHERE v.ID_VENDEDOR = ?;`;
                break;
            }
            case 'MODELO': {
                sqlConsultaDatos = `
                SELECT ID_MODELO, NOMBRE_MODELO, MARCA_MODELO_FK 
                FROM MODELO
                WHERE ID_MODELO = ?`;
                break;
            }
            case 'CATEGORIAS': {
                sqlConsultaDatos = `
                SELECT ID_CATEGORIAS_PIEZAS, NOMBRE 
                FROM CATEGORIAS_PIEZAS
                WHERE ID_CATEGORIAS_PIEZAS = ?`;
                break;
            }
            case 'MARCAS': {
                sqlConsultaDatos = `
                SELECT ID_MARCAS, NOMBRE_MARCA 
                FROM MARCAS
                WHERE ID_MARCAS = ?`;
                break;
            }
            case 'NOMINAS': {
                break;
            }
            case 'VACACIONES': {
                break;
            }
            case 'PIEZAS': {
                sqlConsultaDatos = `
                SELECT p.ID_PIEZAS, p.DESCRIPCION, p.PESO, p.PRECIO_COMPRA, p.PRECIO, p.REFERENCIA, p.FECHA_YEAR, p.FECHA_INICIO_VENTA, p.IMAGENES, 
                c.ID_CATEGORIAS_PIEZAS AS CATEGORIAS, v.ID_VENDEDOR AS RAZON_SOCIAL, e.ID_EMPLEADOS AS EMPLEADO, f.ID_FACTURAS AS FACTURA, m.ID_MODELO AS NOMBRE_MODELO, 
                p.VENDIDO, mar.ID_MARCAS AS NOMBRE_MARCA, cli.ID_CLIENTE AS CLIENTE, pm.ID_PIEZAS_MODELOS AS PIEZA_MODELO  
                FROM PIEZAS p
                LEFT JOIN CATEGORIAS_PIEZAS c ON p.CATEGORIAS_FK = c.ID_CATEGORIAS_PIEZAS
                LEFT JOIN VENDEDOR v ON p.VENDEDOR_FK = v.ID_VENDEDOR
                LEFT JOIN DATOS_COMUNES dc_vendedor ON v.DATOS_COMUNES_FK = dc_vendedor.ID_DATOS_COMUNES
                LEFT JOIN EMPLEADOS e ON p.EMPLEADO_FK = e.ID_EMPLEADOS
                LEFT JOIN DATOS_COMUNES dc_empleado ON e.DATOS_COMUNES_FK = dc_empleado.ID_DATOS_COMUNES
                LEFT JOIN PIEZAS_MODELOS pm ON p.ID_PIEZAS = pm.PIEZAS_FK
                LEFT JOIN MODELO m ON pm.MODELO_FK = m.ID_MODELO
                LEFT JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS 
                LEFT JOIN LINEA_FACTURA lf ON p.ID_PIEZAS = lf.PIEZAS_FK
                LEFT JOIN FACTURA f ON lf.FACTURA_FK = f.ID_FACTURAS
                LEFT JOIN CLIENTE cli ON f.CLIENTE_FK = cli.ID_CLIENTE
                LEFT JOIN DATOS_COMUNES dc_cliente ON cli.DATOS_COMUNES_FK = dc_cliente.ID_DATOS_COMUNES
                WHERE p.ID_PIEZAS = ?`;
                break;
            }
            case 'PIEZAS_MODELOS': {
                break;
            }
            case 'FACTURA': {
                break;
            }
        }

        const resultadoConsulta = await realizarConsulta(sqlConsultaDatos, [id]);

        if (!resultadoConsulta || resultadoConsulta.length === 0) {
            console.error(`❌ Error: datos no encontrados src/api/web_crud.js para tabla: ${tabla}`);
            return res.status(404).json({ error: "Datos no encontrados." });
        }
        const tablas_comprobar = ['EMPLEADOS', 'VENDEDOR', 'CLIENTES'];
        // Proceso de UPDATE para empleados, vendedores y clientes
        if (tablas_comprobar.some(table => table === tabla)) {

            const datosActuales = resultadoConsulta[0];
            const idDatosComunes = datosActuales.DATOS_COMUNES_FK; // Obtener la FK de DATOS_COMUNES

            // Construir los campos a actualizar dinámicamente
            let camposActualizarEmpleados = [];
            let valoresActualizarEmpleados = [];
            let camposActualizarDatosComunes = [];
            let valoresActualizarDatosComunes = [];

            Object.entries(datos).forEach(([key, value]) => {
                if (key === Object.keys(datos)[0]) {
                    return;
                }
                if (value && datosActuales[key] !== value.trim()) {  // Comparar valores
                    if (["NUMERO_SS", "TIPO_CUENTA", "TOTAL_VACACIONES", "DATOS_TARJETA"].includes(key)) {
                        camposActualizarEmpleados.push(`${key} = ?`);
                        valoresActualizarEmpleados.push(value.trim());
                        console.log(`✅ Dato que se va actualizar src/api/web_crud.js/modificarDatos() con ID ${id}`);
                    } else {
                        if (key === 'CODIGO_POSTAL') {
                            if (parseInt(datosActuales[key], 10) === parseInt(value.trim(), 10)) {
                                return;  // Evitar actualizar si no hay cambio
                            }
                        }
                        camposActualizarDatosComunes.push(`${key} = ?`);
                        valoresActualizarDatosComunes.push(value.trim());
                        console.log(`✅ Dato que se va actualizar src/api/web_crud.js/modificarDatos() con ID ${id}`);
                    }
                }
            });

            // Ejecutar actualización solo si hay cambios
            if (camposActualizarEmpleados.length > 0) {
                const sqlUpdateEmpleados = `UPDATE ${tabla} SET ${camposActualizarEmpleados.join(', ')} WHERE ID_${tabla} = ?;`;
                valoresActualizarEmpleados.push(id);
                console.log(`Ejecutando actualización en la tabla: ${tabla}`);
                await realizarConsulta(sqlUpdateEmpleados, valoresActualizarEmpleados);
            }
            // Ejecutar actualización solo si hay cambios
            if (camposActualizarDatosComunes.length > 0) {
                const sqlUpdateDatosComunes = `UPDATE DATOS_COMUNES SET ${camposActualizarDatosComunes.join(', ')} WHERE ID_DATOS_COMUNES = ?;`;
                valoresActualizarDatosComunes.push(idDatosComunes);
                console.log(`Ejecutando actualización datos comunes`);
                await realizarConsulta(sqlUpdateDatosComunes, valoresActualizarDatosComunes);
            }

            return res.status(200).json({ mensaje: "Empleado actualizado correctamente" });
        }

        // UPDATE para tabla PIEZAS
        if (tabla.toUpperCase() === 'PIEZAS') {
            for (let i = 0; i < realizarConsulta.length; i++) {
                let datosActuales = resultadoConsulta[i];
                // Formatear la fecha antes de realizar la consulta
                const fechaInicioVentaFormateada = formatearFecha(datos.FECHA_INICIO_VENTA);
                // Comenzamos con el proceso de actualización solo si los datos son diferentes
                const queries = [];
                const parametros = [];

                // Compara cada campo y añade la consulta de actualización si hay cambios
                if (datos.DESCRIPCION !== datosActuales.DESCRIPCION) {
                    queries.push("UPDATE PIEZAS SET DESCRIPCION = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.DESCRIPCION, id]);
                }

                if (datos.PESO !== datosActuales.PESO) {
                    queries.push("UPDATE PIEZAS SET PESO = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.PESO, id]);
                }

                if (datos.PRECIO_COMPRA !== datosActuales.PRECIO_COMPRA) {
                    queries.push("UPDATE PIEZAS SET PRECIO_COMPRA = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.PRECIO_COMPRA, id]);
                }

                if (datos.PRECIO !== datosActuales.PRECIO) {
                    queries.push("UPDATE PIEZAS SET PRECIO = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.PRECIO, id]);
                }

                if (datos.REFERENCIA !== datosActuales.REFERENCIA) {
                    queries.push("UPDATE PIEZAS SET REFERENCIA = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.REFERENCIA, id]);
                }

                if (datos.FECHA_YEAR !== datosActuales.FECHA_YEAR) {
                    queries.push("UPDATE PIEZAS SET FECHA_YEAR = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.FECHA_YEAR, id]);
                }

                if (datos.FECHA_INICIO_VENTA !== datosActuales.FECHA_INICIO_VENTA) {
                    queries.push("UPDATE PIEZAS SET FECHA_INICIO_VENTA = ? WHERE ID_PIEZAS = ?");
                    parametros.push([fechaInicioVentaFormateada, id]);
                }

                if (datos.IMAGENES !== datosActuales.IMAGENES) {
                    queries.push("UPDATE PIEZAS SET IMAGENES = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.IMAGENES, id]);
                }

                if (datos.CATEGORIAS !== datosActuales.CATEGORIAS) {
                    queries.push("UPDATE PIEZAS SET CATEGORIAS_FK = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.CATEGORIAS, id]);
                }

                if (datos.RAZON_SOCIAL !== datosActuales.RAZON_SOCIAL) {
                    queries.push("UPDATE PIEZAS SET VENDEDOR_FK = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.RAZON_SOCIAL, id]);
                }

                if (datos.EMPLEADO !== datosActuales.EMPLEADO) {
                    queries.push("UPDATE PIEZAS SET EMPLEADO_FK = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.EMPLEADO, id]);
                }

                if (datos.VENDIDO !== datosActuales.VENDIDO) {
                    queries.push("UPDATE PIEZAS SET VENDIDO = ? WHERE ID_PIEZAS = ?");
                    parametros.push([datos.VENDIDO, id]);
                }

                if (datos.NOMBRE_MODELO !== datosActuales.NOMBRE_MODELO) {
                    queries.push("UPDATE PIEZAS_MODELOS SET MODELO_FK = ? WHERE ID_PIEZAS_MODELOS = ?");
                    parametros.push([datos.NOMBRE_MODELO, datos.ID_PIEZAS_MODELOS]);
                }

                if (datos.NOMBRE_MARCA !== datosActuales.NOMBRE_MARCA) {
                    queries.push("UPDATE MODELO SET MARCA_MODELO_FK = ? WHERE ID_MODELO = (SELECT MODELO_FK FROM PIEZAS_MODELOS WHERE PIEZAS_FK = ? LIMIT 1)");
                    parametros.push([datos.NOMBRE_MARCA, id]);
                }

                if (datos.CLIENTE !== datosActuales.CLIENTE) {
                    queries.push("UPDATE FACTURA SET CLIENTE_FK = ? WHERE ID_FACTURAS = (SELECT FACTURA_FK FROM LINEA_FACTURA WHERE PIEZAS_FK = ?)");
                    parametros.push([datos.CLIENTE, id]);
                }

                // Verificar si se deben realizar cambios
                if (queries.length > 0) {
                    for (let i = 0; i < queries.length; i++) {
                        await realizarConsulta(queries[i], parametros[i]);
                    }
                    return res.status(200).json({ mensaje: "Datos actualizados correctamente." });
                } else {
                    return res.status(200).json({ mensaje: "No hubo cambios en los datos." });
                }
            }
        }


        const datosActuales = resultadoConsulta[0];

        let camposActualizar = [];
        let valoresActualizar = [];

        Object.entries(datos).forEach(([key, value]) => {
            if (key === Object.keys(datos)[0]) {
                return;
            }
            if (value && datosActuales[key] !== value.trim()) {
                camposActualizar.push(`${key} = ?`);
                valoresActualizar.push(value.trim());
                console.log(`✅ Dato que se va actualizar src/api/web_crud.js/modificarDatos() con ID ${id}`);
            }
        });

        if (camposActualizar.length > 0) {
            const sqlUpdate = `UPDATE ${tabla} SET ${camposActualizar.join(', ')} WHERE ID_${tabla} = ?;`;
            valoresActualizar.push(id);
            console.log(`Ejecutando actualización en la tabla: ${tabla}`);
            await realizarConsulta(sqlUpdate, valoresActualizar);
        }

        return res.status(200).json({ mensaje: "Datos actualizados correctamente" });
    } catch (error) {
        console.error(`❌ Error src/api/web_crud.js al modificar en ${tabla}: ${error}`);
        return res.status(500).json({ error: `Error al modificar Empleado ID ${datos.ID_EMPLEADOS}` });
    }
}


function formatearFecha(fecha) {
    const date = new Date(fecha);
    // Formateamos la fecha como 'YYYY-MM-DD HH:MM:SS'
    return date.toISOString().slice(0, 19).replace('T', ' ');
}