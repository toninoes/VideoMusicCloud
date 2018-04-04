package vmc.exception;

public class CampoUnicoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String nombreRecurso;
    private String nombreCampo;
    private Object valorCampo;

    public CampoUnicoException( String nombreRecurso, String nombreCampo, Object valorCampo) {
        super(String.format("Ya existe un %s con %s : '%s'", nombreRecurso, nombreCampo, valorCampo));
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
