package vmc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class LocationConfig {

    /**
     * Localización de los vídeos, puede utilizarse ruta absoluta o relativa
     * como pueden verse en las siguientes líneas comentadas, si el directorio
     * 'videos' no existe, lo crea
     */
	
	// ubicación dentro del mismo proyecto. en una ubicación concreta
    //private String location = "src/main/resources/static/videos";
	
	// fuera del proyecto en una ruta absoluta de un servidor Gnu/Linux
	//private String location = "/home/toni/videos";
	
	//o en una partición concreta de un equipo Windows.
	private String location = "../videos";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Localización de los fotos, puede utilizarse ruta absoluta o relativa
     * como pueden verse en las siguientes líneas comentadas, si el directorio
     * 'fotos' no existe, lo crea
     */
	
	// ubicación dentro del mismo proyecto. en una ubicación concreta
    //private String location = "src/main/resources/static/fotos";
	
	// fuera del proyecto en una ruta absoluta de un servidor Gnu/Linux
	//private String location = "/home/toni/fotos";
	
	//o en una partición concreta de un equipo Windows.
	
    private String locationFotos = "../usuarios/fotoPerfil";

    public String getLocationFotos() {
        return locationFotos;
    }

    public void setLocationFotos(String locationFotos) {
        this.locationFotos = locationFotos;
    }

}
