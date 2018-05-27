package vmc.modeltest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import vmc.model.Genero;
import vmc.model.Usuario;
import vmc.model.Video;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class VideoTest {

	@Mock
	Usuario usuarioMock1;
	@Mock
	Usuario usuarioMock2;
	@Mock
	Set<Genero> videoGenerosMock;

	@InjectMocks
	Video video;
	
	@Test
	public void VideoTesting() {
		Video video = new Video("Titulazo", "Nombraco", usuarioMock1, "Pedazo Video");
		assertEquals(video.getTitulo(), "Titulazo");
		assertEquals(video.getNombre(), "Nombraco");
		assertEquals(video.getUsuario(), usuarioMock1);
		assertEquals(video.getDescripcion(), "Pedazo Video");
		
		video.setTitulo("Titulo del copón");
		video.setNombre("Nombre del copón");
		video.setUsuario(usuarioMock2);
		video.setDescripcion("Descripcion del copón");
		video.setVideoGeneros(videoGenerosMock);
		video.setVisualizaciones(5);
		video.setLikes(5);
		video.setPrivado(true);
		assertEquals(video.getTitulo(), "Titulo del copón");
		assertEquals(video.getNombre(), "Nombre del copón");
		assertEquals(video.getUsuario(), usuarioMock2);
		assertEquals(video.getDescripcion(), "Descripcion del copón");
		assertEquals(video.getVideoGeneros(), videoGenerosMock);
		assertEquals(video.getVisualizaciones(), 5);
		assertEquals(video.getLikes(), 5);
		assertEquals(video.isPrivado(), true);
	}

}
