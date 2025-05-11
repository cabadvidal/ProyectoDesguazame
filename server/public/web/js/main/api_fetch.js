/**
 * Obtiene los nombres de las columnas a mostrar de la tabla.
 * @param {String} tabla Nombre de la tabla a consultar.
 * @returns {Promise<Object>} Devuelve el nombre de las columnas para poder agregar los datos.
 */
async function obtenerNombresColumnas(tabla) {
    return new Promise((resolve, reject) => {
        confirmarConexion(`Solicitar datos para agregar a la tabla: ${tabla}`)
            .then((valido) => {
                if (!valido) {
                    console.log("Conexión no válida");
                    popUpError('❌ No se han podido obtener datos.');
                    reject("Conexión no válida");
                    return;
                }

                fetch(`/ObtenerNombreColumnas`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ tabla: tabla })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log("Datos recibidos:", data);
                        resolve(data);
                    })
                    .catch(error => {
                        console.error("Error al obtener los datos:", error);
                        popUpError('❌ No se han podido obtener datos.');
                        reject(error);
                    });
            })
            .catch(() => {
                console.log("Error al confirmar conexión");
                popUpError('❌ No se han podido obtener datos.');
                reject("Error al confirmar conexión");
            });
    });
}
/**
 * Obtiene los datos para generar select con datos asociados a FK
 * @param {sttring} tabla - Contiene el nombre de la tabla
 * @returns {Promise<Object>} Devuelve un objecto json con los datos.
 */
async function obtenerDatosFK(tabla) {
    return new Promise((resolve, reject) => {
        confirmarConexion(`Solicitar datos para clave foranea de la tabla: ${tabla}`)
            .then((valido) => {
                if (!valido) {
                    console.log("Conexión no válida");
                    popUpError('❌ No se han podido obtener datos.');
                    reject("Conexión no válida");
                    return;
                }

                fetch(`/ObtenerDatosClaveForanea`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ tabla: tabla })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log("Datos recibidos:", data);
                        if(data.error) {
                            popUpError(`❌ ${data.error}`);
                        }
                        resolve(data);
                    })
                    .catch(error => {
                        console.error("Error al obtener los datos:", error);
                        popUpError('❌ No se han podido obtener datos.');
                        reject(error);
                    });
            })
            .catch(() => {
                console.log("Error al confirmar conexión");
                popUpError('❌ No se han podido obtener datos.');
                reject("Error al confirmar conexión");
            });
    });
}

/**
 * Obtiene los datos para la consulta de la tabla, con o sin datos de búsqueda.
 * @param {String} tabla - Contiene el nombre de la consulta.
 * @param {Object} [datos={}] - (Opcional) Datos de búsqueda para la consulta.
 * @returns {Promise<Object>} Devuelve un objeto JSON con los datos.
 */
async function consultarDatos(tabla, datos = {}) {
    return new Promise((resolve, reject) => {
        confirmarConexion(`Solicitar datos para la tabla: ${tabla}`)
            .then((valido) => {
                if (!valido) {
                    console.log("Conexión no válida");
                    popUpError('❌ No se han podido obtener datos.');
                    reject("Conexión no válida");
                    return;
                }

                fetch(`/ObtenerDatosTablas`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ datos: datos, tabla: tabla })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log("Datos recibidos:", data);
                        if (data.error) {
                            popUpError(`❌ ${data.error}`);
                        }
                        resolve(data);
                    })
                    .catch(error => {
                        console.error("Error al obtener los datos:", error);
                        popUpError('❌ No se han podido obtener datos.');
                        reject(error);
                    });
            })
            .catch(() => {
                console.log("Error al confirmar conexión");
                popUpError('❌ No se han podido obtener datos.');
                reject("Error al confirmar conexión");
            });
    });
}

/**
 * Agrega datos a una tabla enviándolos al servidor.
 * @param {Object} datos - Datos a agregar.
 * @param {string} tabla - Nombre de la tabla donde se agregarán los datos.
 * @returns {Promise<Object>} - Respuesta del servidor.
 */
async function agregarDatos(datos, tabla) {
    return new Promise((resolve, reject) => {
        confirmarConexion(`Agregar datos para la tabla: ${tabla}`)
            .then((valido) => {
                if (!valido) {
                    console.log("Conexión no válida");
                    popUpError('❌ No se han podido obtener datos.');
                    reject("Conexión no válida");
                    return;
                }

                fetch(`/AgregarDatosTabla`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ datos: datos, tabla: tabla })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log("Datos recibidos:", data);
                        resolve(data);
                    })
                    .catch(error => {
                        console.error("Error al obtener los datos:", error);
                        popUpError('❌ No se han podido obtener datos.');
                        reject(error);
                    });
            })
            .catch(() => {
                console.log("Error al confirmar conexión");
                popUpError('❌ No se han podido obtener datos.');
                reject("Error al confirmar conexión");
            });
    });
}

/**
 * Agrega datos a una tabla enviándolos al servidor.
 * @param {FormData} formData - Datos a agregar.
 * @param {string} tabla - Nombre de la tabla donde se agregarán los datos.
 * @returns {Promise<Object>} - Respuesta del servidor.
 */
async function agregarDatosFormData(formData, tabla) {
    // Aseguramos que la tabla esté en el FormData
    formData.append('tabla', tabla);

    return fetch('/AgregarDatosTablaPiezas', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        console.log('Datos recibidos:', data);
        return data;
    })
    .catch(error => {
        console.error('Error al enviar los datos:', error);
        return { error: 'No se pudieron enviar los datos.' };
    });
}

/**
 * Actualiza datos de una tabla enviándolos al servidor.
 * @param {Object} datos - Datos a actualizar.
 * @param {string} tabla - Nombre de la tabla donde se actualizarán los datos.
 * @returns {Promise<Object>} - Respuesta del servidor.
 */
async function actualizarDatos(datos, tabla) {
    return new Promise((resolve, reject) => {
        confirmarConexion(`Modificar datos para la tabla: ${tabla}`)
            .then((valido) => {
                if (!valido) {
                    console.log("Conexión no válida");
                    popUpError('❌ No se han podido obtener datos.');
                    reject("Conexión no válida");
                    return;
                }

                fetch(`/ModificarDatosTabla`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ datos: datos, tabla: tabla })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log("Datos recibidos:", data);
                        resolve(data);
                    })
                    .catch(error => {
                        console.error("Error al obtener los datos:", error);
                        popUpError('❌ No se han podido obtener datos.');
                        reject(error);
                    });
            })
            .catch(() => {
                console.log("Error al confirmar conexión");
                popUpError('❌ No se han podido obtener datos.');
                reject("Error al confirmar conexión");
            });
    });
}

/**
 * Elimina datos de una tabla enviándolos al servidor.
 * @param {Object} datos - Datos a eliminar.
 * @param {string} tabla - Nombre de la tabla donde se eliminarán los datos.
 * @returns {Promise<Object>} - Respuesta del servidor.
 */
async function eliminarDatos(datos, tabla) {
    return new Promise((resolve, reject) => {
        confirmarConexion(`Eliminar datos para la tabla: ${tabla}`)
            .then((valido) => {
                if (!valido) {
                    console.log("Conexión no válida");
                    popUpError('❌ No se han podido obtener datos.');
                    reject("Conexión no válida");
                    return;
                }

                fetch(`/EliminarRegistrosTabla`, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ datos: datos, tabla: tabla })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log("Datos recibidos:", data);
                        resolve(data);
                    })
                    .catch(error => {
                        console.error("Error al obtener los datos:", error);
                        popUpError('❌ No se han podido obtener datos.');
                        reject(error);
                    });
            })
            .catch(() => {
                console.log("Error al confirmar conexión");
                popUpError('❌ No se han podido obtener datos.');
                reject("Error al confirmar conexión");
            });
    });
}