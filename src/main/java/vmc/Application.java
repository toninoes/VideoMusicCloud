package vmc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import vmc.config.LocationConfig;
import vmc.model.Genero;
import vmc.model.Rol;
import vmc.model.Usuario;
import vmc.repository.GeneroRepository;
import vmc.repository.RolRepository;
import vmc.repository.UsuarioRepository;
import vmc.service.AlmacenamientoService;
import vmc.service.UsuarioService;

@SpringBootApplication
@EnableConfigurationProperties(LocationConfig.class)
public class Application extends SpringBootServletInitializer {
	
	@Autowired
	private RolRepository rolRep;	
	
	@Autowired
	private UsuarioRepository usrRep;	
	
	@Autowired
	private GeneroRepository genRep;
	
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
			if (usrRep.findAll().isEmpty()) {		
				Usuario administrador = new Usuario("admin", "admin", "admin@admin.com", "123456", "img/ava_10.jpg", "salir1/musica1/dinero1/deporte1/");
				usrRep.save(administrador);
				
				rolRep.save(new Rol("ADMIN"));
				rolRep.save(new Rol("USER"));
				
				usrSrv.create(administrador, true);
			}
			
			if(genRep.findAll().isEmpty()) {
				genRep.save(new Genero("rock "));			genRep.save(new Genero("blues"));
				genRep.save(new Genero("ambiente"));		genRep.save(new Genero("regueton"));
				genRep.save(new Genero("clasica"));			genRep.save(new Genero("techno"));
				genRep.save(new Genero("country"));			genRep.save(new Genero("trap"));
				genRep.save(new Genero("disco"));			genRep.save(new Genero("pop"));
				genRep.save(new Genero("drum"));			genRep.save(new Genero("piano"));
				genRep.save(new Genero("electrónica"));		genRep.save(new Genero("metal"));
				genRep.save(new Genero("folk"));			genRep.save(new Genero("latina"));
				genRep.save(new Genero("flamenco"));		genRep.save(new Genero("hip-hop"));
				genRep.save(new Genero("jazz"));			genRep.save(new Genero("house"));
			}
		};
	}
}
