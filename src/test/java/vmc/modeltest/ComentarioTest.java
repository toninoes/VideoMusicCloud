package vmc.modeltest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import vmc.model.Comentario;
import vmc.model.Usuario;
import vmc.model.Video;

@RunWith(MockitoJUnitRunner.class)
public class ComentarioTest {

	@Mock
	Usuario usuarioMock;
	@Mock
	Video videoMock;
	
	@InjectMocks
	Comentario comentario;
	
	@Test
	public void ComentarioTesting() {
		Comentario comentario = new Comentario("Mi primer comentario");
		assertEquals(comentario.getDescripcion(), "Mi primer comentario");
		
		comentario.setDescripcion("Mi segundo comentario");
		comentario.setUsuario(usuarioMock);
		comentario.setVideo(videoMock);
		comentario.setGusta(true);
		assertEquals(comentario.getDescripcion(), "Mi segundo comentario");
		assertEquals(comentario.getUsuario(), usuarioMock);
		assertEquals(comentario.getVideo(), videoMock);
		assertEquals(comentario.isGusta(), true);
	}

}
