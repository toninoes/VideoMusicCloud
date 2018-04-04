package vmc.exception;

public class AlmacenamientoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlmacenamientoException(String message) {
        super(message);
    }

    public AlmacenamientoException(String message, Throwable cause) {
        super(message, cause);
    }
}
