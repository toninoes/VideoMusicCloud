package vmc.modeltest;

import static org.junit.Assert.*;

import org.junit.Test;

import vmc.model.Genero;

public class GeneroTest {

	@Test
	public void GeneroTesting() {
		Genero genero = new Genero("Aflamencao");
		assertEquals(genero.getNombre(), "Aflamencao");
		
		genero.setNombre("Carnaval");
		assertEquals(genero.getNombre(), "Carnaval");
	}

}
