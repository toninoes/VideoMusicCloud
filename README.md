# VideoMusicCloud - Proyectos Informáticos 2018 - Universidad de Cádiz

Aplicación web en Java de repositorio de videos musicales


### Participantes en el proyecto

- José María García Sánchez
- Antonio Ruiz Rondán
- Antonio José Rodríguez Cárdenas
- Luis Fernando Pérez Peregrino
- Andrés Martínez Gavira
- Santiago Zaldívar Lavalle


### Herramientas-Tecnologías utilizadas

- Java SE Development Kit 8
- Thymeleaf
- Spring Boot
- MySQL Community Server 5
- Apache Maven 3
- Apache Tomcat 8
- Git


### Sincronizar el repositorio

- La primera vez: ejecutar los siguientes comandos en el directorio donde vaya a almacenar los archivos de la aplicación ("workspace"):

```sh
pinf2018@vmc ~/workspace $ mkdir VideoMusicCloud
pinf2018@vmc ~/workspace $ cd VideoMusicCloud
pinf2018@vmc ~/workspace/VideoMusicCloud $ git init
pinf2018@vmc ~/workspace/VideoMusicCloud $ git remote add origin https://github.com/toninoes/VideoMusicCloud.git
pinf2018@vmc ~/workspace/VideoMusicCloud $ git pull origin master
```

- El resto de las veces: para descargar archivos con las últimas modificaciones de los demás participantes en el proyecto, ubicados ya dentro del directorio del proyecto ("~/workspace/VideoMusicCloud"):

```sh
pinf2018@vmc ~/workspace/VideoMusicCloud $ git pull origin master
```

- Tras realizar nuestras propias aportaciones/modificaciones, para subir nuevos cambios al repositorio:

```sh
pinf2018@vmc ~/workspace/VideoMusicCloud $ git add --all
pinf2018@vmc ~/workspace/VideoMusicCloud $ git commit -m "Descripción de nuestras aportaciones/modificaciones en el proyecto"
pinf2018@vmc ~/workspace/VideoMusicCloud $ git push origin master
```