package vmc.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "genero_video", 
			   joinColumns = @JoinColumn(name = "genero_id", referencedColumnName = "id"), 
			   inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id", nullable = false))
	private Set<Video> videos;
	
	public Genero() {
		super();
	}
	
	public Genero(@NotBlank String nombre) {
		this.nombre = nombre;
	}
	
	public String getNombre() {return this.nombre;}
	public void setNombre() { this.nombre = nombre;}
	
}