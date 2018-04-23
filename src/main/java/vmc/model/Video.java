package vmc.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "videos")
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotBlank
	private String nombre;
	
	@NotBlank(message = "*Introduzca un título para el vídeo")
	private String titulo;
	
	private String descripcion;
	
	@ManyToOne
	@JoinColumn(name="usuario_id", nullable = false)
	private Usuario usuario;
	
	@ManyToMany(targetEntity = Genero.class, cascade = CascadeType.ALL)
	@JoinTable(name = "video_genero", 
			   joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"), 
			   inverseJoinColumns = @JoinColumn(name = "genero_id", referencedColumnName = "id"))
	private Set<Genero> videogeneros;
	
	private long visualizaciones;
	
	private long likes;
	
	private boolean privado;

	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date creacion;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date actualizacion;

	public Video() {
		super();
	}

	public Video(@NotBlank String titulo, @NotBlank String nombre, Usuario usuario, String descripcion) {
		super();
		this.titulo = titulo;
		this.nombre = nombre;
		this.usuario = usuario;
		this.descripcion = descripcion;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public void setVideoGeneros(Set<Genero> videogeneros) {
		this.videogeneros = videogeneros;
	}
	
	public Set<Genero> getVideoGeneros() {
		return videogeneros;
	}

	public long getId() {
		return id;
	}

	public Date getCreacion() {
		return creacion;
	}

	public Date getActualizacion() {
		return actualizacion;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isPrivado() {
		return privado;
	}

	public void setPrivado(boolean privado) {
		this.privado = privado;
	}

	public long getVisualizaciones() {
		return visualizaciones;
	}

	public void setVisualizaciones(long visualizaciones) {
		this.visualizaciones = visualizaciones;
	}

	public long getLikes() {
		return likes;
	}

	public void setLikes(long likes) {
		this.likes = likes;
	}
}
