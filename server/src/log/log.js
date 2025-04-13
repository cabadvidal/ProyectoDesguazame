import { createLogger, format, transports } from "winston";
import DailyRotateFile from "winston-daily-rotate-file";

export const logger = createLogger({
    level: "debug", // Nivel mínimo de log a registrar
    format: format.combine(
        format.timestamp({ format: "DD-MM-YYYY HH:mm:ss" }), // Agrega fecha y hora
        format.printf(({ timestamp, level, message }) => `[${timestamp}] [${level.toUpperCase()}] ${message}`)
    ),
    transports: [
        new DailyRotateFile({
            filename: "logs/server-%DATE%.log",  // Usa %DATE% para generar un archivo por día
            datePattern: "YYYY-MM-DD", // Formato de la fecha en el nombre del archivo
            zippedArchive: true, // Comprime logs antiguos en .gz
            maxSize: "20m", // Tamaño máximo de cada archivo de log antes de crear uno nuevo
            maxFiles: "7d", // Mantiene logs de los últimos 7 días
            level: "debug"  // Guarda todos los niveles de log (incluyendo debug) en el archivo
        }),
        new transports.Console({
            level: "debug", // Muestra todos los niveles de log en consola
            format: format.combine(format.colorize(), format.simple())
        })
    ]
});

// Redirigir console.log, console.warn y console.error a winston
console.log = (...args) => logger.info(args.join(" "));
console.warn = (...args) => logger.warn(args.join(" "));
console.error = (...args) => logger.error(args.join(" "));

