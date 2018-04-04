package vmc.exception;

public class AlmacenamientoFicheroNoEncontradoException extends AlmacenamientoException {

	private static final long serialVersionUID = 1L;

	public AlmacenamientoFicheroNoEncontradoException(String message) {
        super(message);
    }

    public AlmacenamientoFicheroNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}