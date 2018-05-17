package vmc.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import vmc.config.LocationConfig;
import vmc.exception.AlmacenamientoException;
import vmc.exception.AlmacenamientoFicheroNoEncontradoException;

@Service
public class AlmacenamientoService {
	
	private Path rootLocation;
	
	private Path rootLocationFotos;
	
	@Autowired
    public AlmacenamientoService(LocationConfig properties) {
		this.rootLocation = Paths.get(properties.getLocation());
		this.rootLocationFotos = Paths.get(properties.getLocationFotos());
    }

    public void store(MultipartFile file, String option) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new AlmacenamientoException("Error al guardar el fichero vacío " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new AlmacenamientoException(
                        "No se puede almacenar el fichero con una ruta relativa fuera del directorio actual "
                                + filename);
            }
            if(option.equals("video"))
            	Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            else
            	Files.copy(file.getInputStream(), this.rootLocationFotos.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new AlmacenamientoException("Error al almacenar el fichero " + filename, e);
        }
    }
    
    /* Este es el método sobrecargado el que vale*/
    public void store(MultipartFile file, String option, String name) {
        String filename = StringUtils.cleanPath(name);
        try {
            if (file.isEmpty()) {
                throw new AlmacenamientoException("Error al guardar el fichero vacío " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new AlmacenamientoException(
                        "No se puede almacenar el fichero con una ruta relativa fuera del directorio actual "
                                + filename);
            }
            if(option.equals("video"))
            	Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            else
            	Files.copy(file.getInputStream(), this.rootLocationFotos.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new AlmacenamientoException("Error al almacenar el fichero " + filename, e);
        }
    }

    public Stream<Path> loadAll(String option) {
        try {
            if(option.equals("video"))
            	return Files.walk(this.rootLocation, 1)
            				.filter(path -> !path.equals(this.rootLocation))
            				.map(path -> this.rootLocation.relativize(path));
            else
            	return Files.walk(this.rootLocationFotos, 1)
        				.filter(path -> !path.equals(this.rootLocationFotos))
        				.map(path -> this.rootLocationFotos.relativize(path));
        }
        catch (IOException e) {
            throw new AlmacenamientoException("Error al leer los ficheros almacenados", e);
        }

    }

    public Path load(String filename, String option) {
    	if(option.equals("video"))
    		return rootLocation.resolve(filename);
    	else
    		return rootLocationFotos.resolve(filename);
    }

    public Resource loadAsResource(String filename, String option) {
        try {
            Path file = load(filename, option);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new AlmacenamientoFicheroNoEncontradoException(
                        "No se pudo leer el fichero: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new AlmacenamientoFicheroNoEncontradoException("No se pudo leer el fichero: " + filename, e);
        }
    }

    public void delete(String filename, String option) {
        try {
        	if(option.equals("video"))
        		FileSystemUtils.deleteRecursively(rootLocation.resolve(filename));
        	else
        		FileSystemUtils.deleteRecursively(rootLocationFotos.resolve(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void deleteAll(String option) {
    	if(option.equals("video"))
    		FileSystemUtils.deleteRecursively(rootLocation.toFile());
    	else
    		FileSystemUtils.deleteRecursively(rootLocationFotos.toFile());
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocationFotos);
        }
        catch (IOException e) {
            throw new AlmacenamientoException("No se pudo inicializar el almacenamiento", e);
        }
    }
}
