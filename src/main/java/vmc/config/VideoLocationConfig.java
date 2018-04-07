package vmc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class VideoLocationConfig {

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
	private String location = "D:\\videos";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
