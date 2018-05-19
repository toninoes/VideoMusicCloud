#!/bin/bash

printf "[INFO] Creando base de datos para VideoMusicCloud...\n"
printf "[MySQL] Introduzca su password de root de MySQL\n"

if mysql -u root -p < VideoMusicCloud.sql; then
  printf "[OK] Base de datos creada correctamente\n"
else
  printf "[ERROR] No se ha podido crear la base de datos\n"
fi

printf "[INFO] Necesario para el despliegue de VideoMusicCloud...\n"
printf "[Tomcat] Indique directorio de Tomcat.\n"
printf "[Tomcat] Por defecto [/var/lib/tomcat8/webapps]:\n"
printf "[Tomcat] Pulsa ENTER para dejarlo por defecto."

read dir

if [ "$dir" == "" ]; then
  dir="/var/lib/tomcat8/webapps"
fi

printf "[INFO] Desplegando la aplicacion. Espere......\n"
if mvn clean install -Ddir=$dir; then
  printf "[OK] Desplegada correctamente\n"
else
  printf "[ERROR] Ha habido un problema en el despliegue.\n"
fi

printf "[INFO] Reiniciando Tomcat. Espere......\n"
if service tomcat8 restart; then
  sleep 25
  printf "[OK] Tomcat reiniciado\n"
  printf "[OK] Vaya a: http://localhost:8080/VideoMusicCloud\n"
else
  printf "[ERROR] Ha habido un problema al reiniciar Tomcat.\n"
fi
