package vmc.modeltest;

import static org.junit.Assert.*;

import org.junit.Test;

import vmc.model.Rol;

public class RolTest {

	@Test
	public void RolTesting() {
		Rol rol = new Rol("SuperAdmin");
		assertEquals(rol.getRol(), "SuperAdmin");
		
		rol.setRol("MegaAdmin");
		assertEquals(rol.getRol(), "MegaAdmin");
	}

}
