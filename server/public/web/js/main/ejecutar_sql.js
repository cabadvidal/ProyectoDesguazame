async function agregarDatos(datos, tabla) {
    try {
        // Confirmar la conexión antes de enviar los datos
        const valido = await confirmarConexion(`Agregar datos para la tabla: ${tabla}`);
        
        if (!valido) {
            console.log("Conexión no válida");
            return null;
        }

        // Realizar la petición al servidor
        const response = await fetch(`/AgregarDatosTabla`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ tabla, datos }) 
        });

        // Verificar si la respuesta es exitosa
        if (!response.ok) {
            throw new Error(`Error en la petición: ${response.status} ${response.statusText}`);
        }

        // Convertir la respuesta a JSON
        const resultado = await response.json();
        console.log("Datos recibidos:", resultado);

        return resultado; // Devuelve el resultado para que pueda ser utilizado si es necesario

    } catch (error) {
        console.error("Error en agregarDatos:", error);
        return null;
    }
}