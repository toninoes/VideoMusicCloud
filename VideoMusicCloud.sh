#!/bin/bash

printf "[INFO] Creando base de datos para VideoMusicCloud...\n"
printf "[MySQL] Introduzca su contraseña de root de MySQL\n"

if mysql -u root -p < VideoMusicCloud.sql; then
  printf "[OK] Base de datos creada correctamente\n"
else
  printf "[ERROR] No se ha podido crear la base de datos\n"
fi

printf "[INFO] Dato necesario para el despliegue de VideoMusicCloud...\n"
printf "[Tomcat] Indique directorio de destino de Tomcat para ubicar fichero .war\n"
printf "[Tomcat] Pulse enter si quiere que sea [/var/lib/tomcat8/webapps]: "

read dir

if [ "$dir" == "" ]; then
  dir="/var/lib/tomcat8/webapps"
fi

printf "[INFO] Desplegando la aplicación. Espere......\n"
if mvn clean install -Ddir=$dir; then
  printf "[OK] Aplicación desplegada correctamente\n"
else
  printf "[ERROR] Ha habido un problema al desplegar la aplicación.\n"
fi

printf "[INFO] Reiniciando Tomcat. Espere......\n"
if service tomcat8 restart; then
  sleep 25
  printf "[OK] Tomcat reiniciado, vaya a: http://localhost:8080/VideoMusicCloud\n"
else
  printf "[ERROR] Ha habido un problema al reiniciar Tomcat.\n"
fi
