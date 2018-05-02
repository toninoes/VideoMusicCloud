package vmc.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "generos")
public class Genero {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotBlank(message = "*Introduzca un nombre de género")
	private String nombre;

	public Genero() {
		super();
	}
	
	public Genero(@NotBlank String nombre) {
		super();
		this.nombre = nombre;
	}
	
	public long getId() {
		return id;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public void setNombre(String nombre) { 
		this.nombre = nombre;
	}
}