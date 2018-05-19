--
-- Crear base de datos: `videomusiccloud`
--
CREATE DATABASE IF NOT EXISTS `videomusiccloud` DEFAULT CHARACTER SET utf8;

--
-- Damos todos los privilegios al usuario: VideoMusicCloud 
-- con clave: VideoMusicCloud sobre BBDD: videomusiccloud
-- Si el usuario no existe lo crea
--
GRANT ALL ON `videomusiccloud`.* TO 'VideoMusicCloud'@'localhost' IDENTIFIED BY 'VideoMusicCloud';

USE `videomusiccloud`;
