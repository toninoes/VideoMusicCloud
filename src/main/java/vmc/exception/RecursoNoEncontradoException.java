package vmc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND) // éste es un error 404
public class RecursoNoEncontradoException extends RuntimeException {
 
	private static final long serialVersionUID = -8262049727466664556L;
	private String nombreRecurso;
    private String nombreCampo;
    private Object valorCampo;

    public RecursoNoEncontradoException(String nombreRecurso, String nombreCampo, Object valorCampo) {
        super(String.format("%s no existe con %s : '%s'", nombreRecurso, nombreCampo, valorCampo));
        this.nombreRecurso = nombreRecurso;
        this.nombreCampo = nombreCampo;
        this.valorCampo = valorCampo;
    }

	public String getNombreRecurso() {
		return nombreRecurso;
	}

	public String getNombreCampo() {
		return nombreCampo;
	}

	public Object getValorCampo() {
		return valorCampo;
	}

}