import { realizarConsulta } from "../../mysql/consultas_mysql.js";

/**
 * Método que consulta los datos de una factura y la genera mediante un script de php
 * @param {int} id Contiene el id a realizar la consulta
 * @returns 
 */
export async function generarFactura(id) {
    const sqlPiezas = `SELECT p.REFERENCIA, p.DESCRIPCION, p.PESO, lf.PRECIO, dc.RAZON_SOCIAL
        FROM FACTURA f
        LEFT JOIN LINEA_FACTURA lf ON f.ID_FACTURAS = lf.FACTURA_FK
        LEFT JOIN PIEZAS p ON lf.PIEZAS_FK = p.ID_PIEZAS
        LEFT JOIN VENDEDOR v ON p.VENDEDOR_FK = ID_VENDEDOR
        LEFT JOIN DATOS_COMUNES dc ON v.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES
        WHERE f.ID_FACTURAS = ?`;

    const sqlFactura = `SELECT BASE, IVA, FECHA, SUM(BASE + IVA) AS PRECIO_TOTAL 
        FROM FACTURA
        WHERE ID_FACTURAS = ?`;
    const sqlCliente = `SELECT CONCAT(dc.NOMBRE, ', ', dc.APELLIDO_A, ' ', dc.APELLIDO_B) AS NOMBRE, dc.DNI_CIF, CONCAT(dc.DIRECCION, ', ',  dc.MUNICIPIO, ' CP-', dc.CODIGO_POSTAL) AS DIRECCION,
     dc.MOVIL, dc.MAIL
        FROM FACTURA f
        LEFT JOIN CLIENTE c ON f.CLIENTE_FK = c.ID_CLIENTE
        LEFT JOIN DATOS_COMUNES dc ON c.DATOS_COMUNES_FK = dc.ID_DATOS_COMUNES
        WHERE f.ID_FACTURAS = ?`;

    const resultadoPiezas = await realizarConsulta(sqlPiezas, [id]);
    const resultadoFactura = await realizarConsulta(sqlFactura, [id]);
    const resultadoCliente = await realizarConsulta(sqlCliente, [id]);
    const datosEmpresa = {
        DIRECCION: 'Calle Falsa 123',
        CIUDAD: 'Barcelona',
        MUNICIPIO: 'Barcelona',
        CODIGO_POSTAL: '08080',
        DNI_CIF: 'B12345678',
        MOVIL: '611223344',
        MAIL: 'ventas@desguazame.com',
        RAZON_SOCIAL: 'DESGUAZAME S.L.'
    }

    // Enviar los datos al script PHP para generar el PDF
    const response = await fetch('http://localhost/desguace/server/src/php/generar_pdf.php', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            id_factura: id,
            piezas: resultadoPiezas,
            factura: resultadoFactura,
            cliente: resultadoCliente,
            empresa: datosEmpresa
        })
    });

    const texto = await response.text(); // lee como texto para depurar
    console.log("📄 Response completo:");
    console.log("✅ Status:", response.status);
    console.log("✅ Headers:", [...response.headers.entries()]);
    console.log("✅ Body (texto):", texto);
    try {
        const resultado = JSON.parse(texto); // luego parsea a JSON

        if (resultado.status === "0") {
            return true;
        } else {
            console.error("❌ Error al generar factura:", resultado);
            return false;
        }
    } catch (err) {
        console.error("❌ Error al parsear JSON:", texto);
        return false;
    }
}

