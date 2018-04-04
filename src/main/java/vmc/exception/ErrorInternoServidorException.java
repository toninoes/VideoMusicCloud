package vmc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR) // éste es un error 500
public class ErrorInternoServidorException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String operacion;
	private String nombreRecurso;
    private Object valorCampo;
    private String mensaje;
    
    public ErrorInternoServidorException(String op, String nr, Object vc, String msg) {
        super(String.format("No ha sido posible %s %s con id: '%s'\n Error: %s", op, nr, vc, msg));
        this.nombreRecurso = nr;
        this.operacion = op;
        this.valorCampo = vc;
        this.mensaje = msg;        
    }

	public String getOperacion() {
		return operacion;
	}

	public String getNombreRecurso() {
		return nombreRecurso;
	}

	public Object getValorCampo() {
		return valorCampo;
	}

	public String getMensaje() {
		return mensaje;
	}	
	
	

}
