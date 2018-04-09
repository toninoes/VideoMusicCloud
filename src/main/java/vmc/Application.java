package vmc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import vmc.config.VideoLocationConfig;
import vmc.model.Rol;
import vmc.model.Usuario;
import vmc.repository.RolRepository;
import vmc.repository.UsuarioRepository;
import vmc.service.AlmacenamientoService;
import vmc.service.UsuarioService;

@SpringBootApplication
@EnableConfigurationProperties(VideoLocationConfig.class)
public class Application extends SpringBootServletInitializer {
	
	@Autowired
	private RolRepository rolRep;	
	
	@Autowired
	private UsuarioRepository usrRep;	
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
    CommandLineRunner init(AlmacenamientoService almacenamientoService) {
        return (args) -> {
            almacenamientoService.init();            
        };
    }
	
	@Bean
	public CommandLineRunner loadData(UsuarioService usrSrv) {
		return (args) -> {
			if (usrRep.findAll().size() == 0) {		
				Usuario administrador = new Usuario("admin", "admin", "admin@admin.com", "123456");
				usrRep.save(administrador);
				
				rolRep.save(new Rol("ADMIN"));
				rolRep.save(new Rol("USER"));
				
				usrSrv.create(administrador, true);
			}
		};
	}

}
