import { realizarConsulta } from "../../mysql/consultas_mysql.js";
import { app } from "../../desguace.js";
import { logger } from "../log/log.js";
import {
    verificarTabla, agregarEmpleado, agregarVendedores, agregarModelos, agregarMarcas, agregarPiezas,
    agregarNominas, modificarDatos, modificarPassword, eliminarRegistros
} from "./funciones_crud.js";
import { upload } from "../../desguace.js";
/**
 * Llamadas a procedimientos para obtener nombre de columnas
 */
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
        case 'NOMINAS':
            sql = 'CALL ObtenerNombresColumnasNominas();';
            break;
        case 'PASSWORD':
            sql = 'CALL ObtenerNombresColumnasPassword();';
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
/**
 * Obtener datos de claves foraneas
 */
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
            sql = `SELECT ID_CATEGORIAS_PIEZAS, NOMBRE_CATEGORIA FROM CATEGORIAS_PIEZAS`;
            break;
        case 'MODELOS':
            sql = `SELECT ID_MODELO, NOMBRE_MODELO FROM MODELO`;
            break;
        case 'FACTURAS':
            sql = `SELECT ID_PIEZAS, REFERENCIA FROM PIEZAS`;
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
    let datos = req.body.datos;
    let campo;
    let valor;
    if (!tabla || !verificarTabla(tabla)) {
        return res.status(400).json({ error: `Tabla no encontrada. ${tabla}` });
    }
    tabla = tabla.toUpperCase();
    let sql;
    switch (tabla) {
        case 'VENDEDORES':
            sql = `SELECT v.ID_VENDEDOR, dc.RAZON_SOCIAL, dc.DNI_CIF, dc.MAIL, dc.DIRECCION, dc.CODIGO_POSTAL,
            dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.NUMERO_CUENTA
            FROM VENDEDOR v 
            LEFT JOIN DATOS_COMUNES dc ON v.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'CLIENTE':
            sql = `SELECT c.ID_CLIENTE, dc.NOMBRE, dc.APELLIDO_A, dc.APELLIDO_B, dc.DIRECCION, dc.CODIGO_POSTAL, 
            dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.MAIL, dc.DNI_CIF, dc.NUMERO_CUENTA, c.DATOS_TARJETA 
            FROM CLIENTE c
            LEFT JOIN DATOS_COMUNES dc ON c.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'EMPLEADOS':
            sql = `SELECT e.ID_EMPLEADOS, dc.NOMBRE, dc.APELLIDO_A, dc.APELLIDO_B, dc.DIRECCION, dc.CODIGO_POSTAL, 
            dc.CIUDAD, dc.MUNICIPIO, dc.TELEFONO, dc.MOVIL, dc.MAIL, dc.DNI_CIF, dc.NUMERO_CUENTA, e.NUMERO_SS, e.TIPO_CUENTA, e.TOTAL_VACACIONES
            FROM EMPLEADOS e 
            JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'NOMINAS':
            sql = `
            SELECT n.ID_NOMINAS, n.SUELDO_BRUTO, n.ESTADO, DATE_FORMAT(n.MES, '%Y-%m') AS MES, n.EMPLEADOS_FK, e.DATOS_COMUNES_FK, dc.NOMBRE, 
            dc.DNI_CIF
            FROM NOMINAS n
            JOIN EMPLEADOS e ON n.EMPLEADOS_FK = e.ID_EMPLEADOS
            JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'FICHAR':
            sql = `
            SELECT f.ID_FICHAJE, f.HORA_ENTRADA, f.HORA_SALIDA, dc.DNI_CIF
            FROM FICHAJE f
            LEFT JOIN EMPLEADOS e ON f.EMPLEADOS_FK = e.ID_EMPLEADOS
            LEFT JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'VACACIONES':
            sql = `
            SELECT v.ID_VACACIONES, v.FECHA_INICIO, v.FECHA_FINAL, v.CONCEDIDAS, dc.DNI_CIF
            FROM VACACIONES v
            LEFT JOIN EMPLEADOS e ON v.EMPLEADOS_FK = e.ID_EMPLEADOS
            LEFT JOIN DATOS_COMUNES dc ON e.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES`;
            break;
        case 'MARCAS':
            sql = `
            SELECT ID_MARCAS, NOMBRE_MARCA FROM MARCAS`;
            break;
        case 'MODELOS':
            tabla = `MODELO`;
            sql = `SELECT m.ID_MODELO, m.NOMBRE_MODELO, mar.NOMBRE_MARCA 
            FROM MODELO m
            JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS`;
            break;
        case 'PIEZAS':
            sql = `SELECT p.ID_PIEZAS, p.DESCRIPCION, p.PESO, p.PRECIO_COMPRA, p.PRECIO, p.REFERENCIA, p.FECHA_YEAR, p.FECHA_INICIO_VENTA, p.IMAGENES, 
            c.NOMBRE_CATEGORIA AS CATEGORIAS, dc_vendedor.RAZON_SOCIAL, dc_empleado.DNI_CIF AS EMPLEADO, 
            p.VENDIDO, m.NOMBRE_MODELO, mar.NOMBRE_MARCA, dc_cliente.DNI_CIF AS CLIENTE, pm.ID_PIEZAS_MODELOS  
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
            LEFT JOIN DATOS_COMUNES dc_cliente ON cli.DATOS_COMUNES_FK = dc_cliente.ID_DATOS_COMUNES`;
            break;
        case 'CATEGORIAS':
            sql = `SELECT ID_CATEGORIAS_PIEZAS, NOMBRE_CATEGORIA FROM CATEGORIAS_PIEZAS`;
            break;
        case 'FACTURAS':
            sql = `SELECT f.ID_FACTURAS, f.BASE, f.IVA, f.TIPO_PAGO, f.FECHA, f.CLIENTE_FK,
            dc.DNI_CIF, lf.PIEZAS_FK, p.REFERENCIA, lf.PRECIO AS PRECIO_PIEZA
            FROM FACTURA f
            JOIN CLIENTE c ON f.CLIENTE_FK = c.ID_CLIENTE
            JOIN DATOS_COMUNES dc ON c.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES
            LEFT JOIN LINEA_FACTURA lf ON f.ID_FACTURAS = lf.FACTURA_FK
            LEFT JOIN PIEZAS p ON lf.PIEZAS_FK = p.ID_PIEZAS`;
            break;
        case 'RESTABLECER_PASS':
            campo = Object.keys(datos)[0].toUpperCase();
            valor = Object.values(datos)[0];
            sql = `SELECT dc.ID_DATOS_COMUNES, dc.NOMBRE, dc.MAIL, dc.DNI_CIF, dc.${campo} 
            FROM DATOS_COMUNES dc 
            WHERE dc.${campo} LIKE LOWER(CONCAT('%', ?, '%'))`;

            break;
        default:
            sql = `SELECT * FROM ?`;
            break;
    }
    logger.debug(`/src/api/web_crud.js POST: ObtenerDatosTablas || tabla consultada: ${tabla}`);
    if (tabla === 'RESTABLECER_PASS') {
        console.warn(`la consulta sql = ${sql} \n
        el valor es = ${valor}`);
        realizarConsulta(sql, [valor])
            .then(resultado => {
                logger.debug(`/src/api/web_crud.js POST: ObtenerDatosTablas || Resultado de la consulta de creación de la tabla ${tabla} ` + JSON.stringify(resultado));
                console.log(`✅ Datos para la tabla ${tabla} obtenidos correctamente.`);
                res.json(resultado);
            })
            .catch(error => {
                console.error(`❌ Error al obtener datos de la tabla ${tabla}: ${error}`);
                res.status(500).json({ error: "Error en la consulta SQL" });
            });
    } else {
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
    }
});

/**
 * Obtener datos piezas para app movil y escritorio
 */
app.get('/ObtenerPiezas', async (req, res) => {
    const SQL = `SELECT p.ID_PIEZAS, p.DESCRIPCION, p.PESO, p.PRECIO, p.REFERENCIA, p.FECHA_YEAR, p.IMAGENES, 
    c.NOMBRE_CATEGORIA AS CATEGORIAS, dc_vendedor.RAZON_SOCIAL, m.NOMBRE_MODELO, mar.NOMBRE_MARCA 
    FROM PIEZAS p
    LEFT JOIN CATEGORIAS_PIEZAS c ON p.CATEGORIAS_FK = c.ID_CATEGORIAS_PIEZAS
    LEFT JOIN VENDEDOR v ON p.VENDEDOR_FK = v.ID_VENDEDOR
    LEFT JOIN DATOS_COMUNES dc_vendedor ON v.DATOS_COMUNES_FK = dc_vendedor.ID_DATOS_COMUNES
    LEFT JOIN EMPLEADOS e ON p.EMPLEADO_FK = e.ID_EMPLEADOS
    LEFT JOIN DATOS_COMUNES dc_empleado ON e.DATOS_COMUNES_FK = dc_empleado.ID_DATOS_COMUNES
    LEFT JOIN PIEZAS_MODELOS pm ON p.ID_PIEZAS = pm.PIEZAS_FK
    LEFT JOIN MODELO m ON pm.MODELO_FK = m.ID_MODELO
    LEFT JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS 
    WHERE p.VENDIDO = FALSE`;

    realizarConsulta(SQL, null)
        .then(resultado => {
            logger.debug(`/src/api/web_crud.js POST: ObtenerPiezas ` + JSON.stringify(resultado));
            console.log(`✅ Datos para la app obtenidos correctamente.`);
            res.json(resultado);
        })
        .catch(error => {
            console.error(`❌ Error al obtener datos de la tabla piezas: ${error}`);
            res.status(500).json({ error: "Error en la consulta SQL" });
        });
});

/**
 * Obtener datos para el menú vista search
 */
app.get('/ObtenerSearch', async (req, res) => {
    console.log("✅ Ruta /ObtenerSearch ha sido llamada");
    const {TABLA, MARCA} = req.query;
    
    if (!TABLA || !verificarTabla(TABLA)) {
        return res.status(400).json({ error: "Tabla no encontrada." });
    }
    let sql = '';

    switch (TABLA) {
        case 'MARCAS':
            sql = `SELECT ID_MARCAS, NOMBRE_MARCA FROM MARCAS`;
            break;
        case 'CATEGORIAS':
            sql = `SELECT ID_CATEGORIAS_PIEZAS, NOMBRE_CATEGORIA FROM CATEGORIAS_PIEZAS`;
            break;
        case 'MODELOS':
            sql = `SELECT m.ID_MODELO, m.NOMBRE_MODELO FROM MODELO m
            LEFT JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS
            WHERE mar.NOMBRE_MARCA = ?`;
            break;
    }
    if (TABLA.includes('MODELOS')) {
        logger.debug(`/src/api/web_crud.js GET: ObtenerSearch || tabla consultada: ${TABLA}`);
    realizarConsulta(sql, [MARCA])
        .then(resultado => {
            logger.debug(`/src/api/web_crud.js GET: ObtenerSearch || Resultado de la consulta de creación de la tabla ${TABLA} ` + JSON.stringify(resultado));
            console.log(`✅ Datos clave foranea obtenidos correctamente para la tabla ${TABLA}`);
            res.json(resultado);
        })
        .catch(error => {
            console.error(`❌ Error al obtener datos de ObtenerSearch: ${error}`);
            res.status(500).json({ error: "Error en la consulta SQL" });
        });
        return;
    }
    logger.debug(`/src/api/web_crud.js GET: ObtenerSearch || tabla consultada: ${TABLA}`);
    realizarConsulta(sql, TABLA)
        .then(resultado => {
            logger.debug(`/src/api/web_crud.js GET: ObtenerSearch || Resultado de la consulta de creación de la tabla ${TABLA} ` + JSON.stringify(resultado));
            console.log(`✅ Datos clave foranea obtenidos correctamente para la tabla ${TABLA}`);
            res.json(resultado);
        })
        .catch(error => {
            console.error(`❌ Error al obtener datos de ObtenerSearch: ${error}`);
            res.status(500).json({ error: "Error en la consulta SQL" });
        });

});

/**
 * Obtener datos de la búsqueda solicitada
 */
app.get('/Search', async (req, res) => {
    const {TEXTO, CATEGORIAS, MARCAS, MODELOS} = req.query;
    let sql = `SELECT p.ID_PIEZAS, p.DESCRIPCION, p.PESO, p.PRECIO, p.REFERENCIA, p.FECHA_YEAR, p.IMAGENES, 
    c.NOMBRE_CATEGORIA AS CATEGORIAS, dc_vendedor.RAZON_SOCIAL, m.NOMBRE_MODELO, mar.NOMBRE_MARCA 
    FROM PIEZAS p
    LEFT JOIN CATEGORIAS_PIEZAS c ON p.CATEGORIAS_FK = c.ID_CATEGORIAS_PIEZAS
    LEFT JOIN VENDEDOR v ON p.VENDEDOR_FK = v.ID_VENDEDOR
    LEFT JOIN DATOS_COMUNES dc_vendedor ON v.DATOS_COMUNES_FK = dc_vendedor.ID_DATOS_COMUNES
    LEFT JOIN EMPLEADOS e ON p.EMPLEADO_FK = e.ID_EMPLEADOS
    LEFT JOIN DATOS_COMUNES dc_empleado ON e.DATOS_COMUNES_FK = dc_empleado.ID_DATOS_COMUNES
    LEFT JOIN PIEZAS_MODELOS pm ON p.ID_PIEZAS = pm.PIEZAS_FK
    LEFT JOIN MODELO m ON pm.MODELO_FK = m.ID_MODELO
    LEFT JOIN MARCAS mar ON m.MARCA_MODELO_FK = mar.ID_MARCAS 
    WHERE p.VENDIDO = FALSE`
     const params = [];

    if (TEXTO && TEXTO.trim() !== "") {
        sql += " AND (p.DESCRIPCION LIKE ? OR p.REFERENCIA LIKE ?)";
        const textoParam = `%${TEXTO}%`;
        params.push(textoParam, textoParam);
    }

    if (CATEGORIAS && CATEGORIAS.trim() !== "") {
        sql += " AND c.NOMBRE_CATEGORIA = ?";
        params.push(CATEGORIAS);
    }

    if (MARCAS && MARCAS.trim() !== "") {
        sql += " AND mar.NOMBRE_MARCA = ?";
        params.push(MARCAS);
    }

    if (MODELOS && MODELOS.trim() !== "") {
        sql += " AND m.NOMBRE_MODELO = ?";
        params.push(MODELOS);
    }

    console.log("🔍 web_crud/Search Consulta de búsqueda dinámica SQL:", sql);
    console.log("📦 web_crud/Search Parámetros de búsqueda:", params);

    try {
        const resultado = await realizarConsulta(sql, params);
        res.json(resultado);
    } catch (error) {
        console.error("❌ Error en /Search:", error);
        res.status(500).json({ error: "Error en la consulta SQL" });
    }
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
        case "PIEZAS":
            await agregarPiezas(datos, res);
            break;
        case "NOMINAS":
            await agregarNominas(datos, res);
            break;
        default:
            return res.status(400).json({ error: "Tabla no soportada." });
    }
});

/**
 * Agregas datos para piezas
 */
app.post('/AgregarDatosTablaPiezas', upload.array('IMAGENES[]'), async (req, res) => {
    let tabla = req.body.tabla;
    if (!tabla) return res.status(400).json({ error: 'Tabla no especificada.' });

    if (!verificarTabla(tabla)) {
        return res.status(400).json({ error: 'Tabla incorrecta.' });
    }

    tabla = tabla.toUpperCase();

    // Guardamos los nombres de archivo en el campo datos.IMAGENES
    const nombresImagenes = req.files.map(file => file.filename).join(';');
    let datos = req.body;
    datos.IMAGENES = nombresImagenes;

    switch (tabla) {
        case 'PIEZAS':
            await agregarPiezas(datos, res);
            break;
        // Otros casos para otras tablas
        default:
            return res.status(400).json({ error: 'Tabla no soportada.' });
    }
});

/**
 * Hacer updates en tablas
 */
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
    console.log(`la tabla q mando es : ${tabla}`)
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
        case "NOMINAS":
            await modificarDatos(datos, res, 'NOMINAS');
            break;
        case "FACTURAS":
            await modificarDatos(datos, res, 'FACTURAS');
            break;
        case "VACACIONES":
            await modificarDatos(datos, res, 'VACACIONES');
            break;
        case "FICHAR":
            await modificarDatos(datos, res, 'FICHAR');
            break;
        case "CLIENTE":
            await modificarDatos(datos, res, 'CLIENTE');
            break;
        case "RESTABLECER_PASS":
            await modificarPassword(datos, res);
            break;
        default:
            return res.status(400).json({ error: "Tabla no soportada." });
    }
});

/**
 * Eliminar registros de la base de datos
 */
app.delete('/EliminarRegistrosTabla', async (req, res) => {
    console.log("✅Ruta /EliminarRegistrosTabla ha sido llamada");

    let { datos, tabla } = req.body;
    if (!datos || !tabla) {
        return res.status(400).json({ error: "Datos incompletos." });
    }
    if (!verificarTabla(tabla)) {
        return res.status(400).json({ error: "Tabla incorrecta." });
    }
    tabla = tabla.toUpperCase();

    console.log(`Datos recibidos en /src/api/web_crud.js los datos son: ${JSON.stringify(datos)}`);
    switch (tabla) {
        case 'CATEGORIAS':
            tabla = 'CATEGORIAS_PIEZAS';
            break;
    }
    eliminarRegistros(datos, tabla, res);
});