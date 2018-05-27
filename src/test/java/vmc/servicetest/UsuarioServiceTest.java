package vmc.servicetest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import vmc.model.Rol;
import vmc.model.Usuario;
import vmc.repository.RolRepository;
import vmc.repository.UsuarioRepository;
import vmc.service.UsuarioService;

public class UsuarioServiceTest {
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Mock
	Usuario usuario;
	
	@Mock
	Rol roles;
	
	@Mock
	RolRepository rolRepository;
	
	@Mock
	UsuarioRepository usuarioRepository;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@InjectMocks
	UsuarioService usuarioService;
	
	@Test
	public void CreateTest() {
		when(usuario.getMail()).thenReturn("manolo@manolo.com");
		when(usuarioRepository.findByMail("manolo@manolo.com")).thenReturn(usuario);
		usuarioService.create(usuario, false);
		assertEquals(usuario, usuarioService.findByMail(usuario.getMail()));
	}
}
