# VideoMusicCloud
Aplicación web en Java de repositorio de videos musicales


## Organización

### Participantes en el proyecto

- **Mánager**: José María García Sánchez. 
- **Analista**: Antonio Ruiz Rondán.
- **Diseñador**: Andrés Martínez Gavira.
- **Programadores**: Antonio Ruiz Rondán, Andrés Martínez Gavira y Luis Fernando Pérez Peregrino.
- **Testers**: Antonio José Rodríguez Cárdenas y Santiago Zaldívar Lavalle.


### Herramientas-Tecnologías utilizadas

- Java SE Development Kit 8
- Thymeleaf
- Spring Boot 2.0.0
- MySQL Community Server 5.7.21
- Apache Maven 3.5.3
- Apache Tomcat 8.0.50
- Git
- Spring Tool Suite 3.9.2

### Capturas de la aplicación funcionando

Vista del perfil de usuario:

![perfil](https://github.com/toninoes/VideoMusicCloud/blob/master/src/main/resources/static/img/perfil.png)

Vista de los listados de vídeos:

![perfil](https://github.com/toninoes/VideoMusicCloud/blob/master/src/main/resources/static/img/listas.png)


## Manual de instalación y explotación

Las instrucciones de instalación y explotación del sistema se detallan a continuación.

### Requisitos previos

Los requerimientos que el sistema debe tener para el correcto funcionamiento. Entre paréntesis las versiones sobre las que se ha trabajado:

- S.O.: Ubuntu Server (versión 18.04 LTS - 64 bits)
- Lenguajes: Java (v. 8)
- Java SE Development Kit 8
- Apache Tomcat 8 (v. 8.0.50)
- MySQL Community Server 5 (v. 5.7.21)
- Apache Maven 3 (v. 3.5.3)

Para tenerlo todo instalado en Ubuntu, simplemente teclear:

```sh
vmc@pinf ~ $ sudo apt-get install openjdk-8-jdk openjdk-8-doc openjdk-8-jre
vmc@pinf ~ $ sudo apt-get install git mysql-server tomcat8 maven
```

### Procedimientos de instalación

Primero clonamos el proyecto:

```sh
vmc@pinf ~ $ cd /tmp
vmc@pinf /tmp $ git clone https://github.com/toninoes/VideoMusicCloud.git
```

Una vez clonado el repositorio, para la correcta instalación y despliegue de la aplicación se necesitará ejecutar el siguiente script en bash (VideoMusicCloud.sh) que contiene a su vez una llamada a otro fichero sql (VideoMusicCloud.sql), ambos scripts se ubican en la raiz de este proyecto:

Por tanto ejecutamos:

```sh
vmc@pinf /tmp $ cd VideoMusicCloud
vmc@pinf /tmp/VideoMusicCloud $ sudo bash VideoMusicCloud.sh
```

### Procedimientos de operación y nivel de servicio

Es preciso asegurarnos de tener correctamente instalado y configurado nuestro gestor de base de datos MySQL, tal y como aparece en el fichero **application.properties**, debe de haber un usuario llamado **VideoMusicCloud** cuya contraseña sea **VideoMusicCloud**.

Obviamente esto debe modificarse en un entorno de producción por los problemas de seguridad que plantearía dejarlo de esta manera, para ello habría que cambiar dicha configuración, las siguientes 2 líneas:

```sh
# ===============================
# = CONFIGURACION DE BBDD
# ===============================
spring.datasource.username=USUARIO_NUEVO
spring.datasource.password=CLAVE_NUEVA
```

Se debe otorgar permisos al usuario **USUARIO_NUEVO** sobre la base de datos: **videomusiccloud**. Esta asignación de privilegios se consigue con la siguiente orden:

```sh
GRANT ALL ON ‘videomusiccloud‘.* TO ’USUARIO_NUEVO’@’localhost’
IDENTIFIED BY ’CLAVE_NUEVA’;
```

### Pruebas de implantación

Tras su ejecución y si todo ha ido bien, la aplicación se encontrará correctamente instalada. Podremos dirigirnos a: [http://localhost:8080/VideoMusicCloud/](http://localhost:8080/VideoMusicCloud/).


