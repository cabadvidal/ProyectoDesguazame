# ruta del fichero /usr/local/bin  backup_mariadb.sh
#!/bin/bash

# Configuración
DB_NAME="DESGUACE"
BACKUP_DIR="/var/backups/mariadb"
DATE=$(date +"%Y-%m-%d")
FILENAME="${DB_NAME}_${DATE}.sql.gz"

# Crear directorio si no existe
mkdir -p "$BACKUP_DIR"

# Dump de la base de datos comprimido
mysqldump "$DB_NAME" | gzip > "$BACKUP_DIR/$FILENAME"

# Eliminar backups con más de 7 días
find "$BACKUP_DIR" -type f -name "${DB_NAME}_*.sql.gz" -mtime +6 -exec rm {} \;

# permisos de ejecución
sudo chmod +x ./backup_mariadb.sh


# agregar al cron
crontab -e
0 2 * * * /usr/local/bin/backup_mariadb.sh

# ruta fichero /root archivo ~/.my.cnf con:
[client]
user=root
password=kalandraka
# permisos al fichero
chmod 600 ~/.my.cnf