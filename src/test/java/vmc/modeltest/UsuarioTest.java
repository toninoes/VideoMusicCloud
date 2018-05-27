package vmc.modeltest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import vmc.model.Rol;
import vmc.model.Usuario;

public class UsuarioTest {
	
	@Mock
	Set<Rol> rolesMock;
	@Mock
	Set<Usuario> siguiendosMock;
	@Mock
	Set<Usuario> seguidoresMock;
	
	@Test
	public void UsuarioTesting() {
		Usuario usuario = new Usuario("Manolo", "García Fernández", "manolo@mail.com", "manolito");
		assertEquals(usuario.getNombre(), "Manolo");
		assertEquals(usuario.getApellidos(), "García Fernández");
		assertEquals(usuario.getMail(), "manolo@mail.com");
		assertEquals(usuario.getPassword(), "manolito");
		assertEquals(usuario.getFoto(), "");
		
		usuario.setNombre("Manolón");
		usuario.setApellidos("Fernández García");
		usuario.setMail("manolon@mail.com");
		usuario.setPassword("manolillo");
		usuario.setActivo(true);
		usuario.setRoles(rolesMock);
		usuario.setSiguiendos(siguiendosMock);
		usuario.setSeguidores(seguidoresMock);
		assertEquals(usuario.getNombre(), "Manolón");
		assertEquals(usuario.getApellidos(), "Fernández García");
		assertEquals(usuario.getMail(), "manolon@mail.com");
		assertEquals(usuario.getPassword(), "manolillo");
		assertEquals(usuario.getActivo(), true);
		assertEquals(usuario.getRoles(), rolesMock);
		assertEquals(usuario.getSiguiendo(), siguiendosMock);
		assertEquals(usuario.getSeguidores(), seguidoresMock);
	}

}
