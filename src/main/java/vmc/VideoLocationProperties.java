package vmc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class VideoLocationProperties {

    /**
     * Localización de los vídeos
     */
    //private String location = "src/main/resources/static/videos";
	private String location = "../videos";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
