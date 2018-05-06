package vmc.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "comentarios")
public class Comentario {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	//@NotBlank(message = "*Introduzca un texto para el comentario")
	private String descripcion;
	
	@ManyToOne
	@JoinColumn(name="usuario_id", nullable = false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="video_id", nullable = false)
	private Video video;
	
	private boolean gusta;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date creacion;
	
	public Comentario() {
		super();
	}
	
	//public Comentario(@NotBlank String descripcion) {
	public Comentario(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public long getId() {
		return id;
	}
	
	public String getDescripcion() {
		return this.descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isGusta() {
		return gusta;
	}

	public void setGusta(boolean gusta) {
		this.gusta = gusta;
	}
	
	public Video getVideo() {
		return video;
	}
	
	public void setVideo(Video video) {
		this.video = video;
	}
	
	public Date getCreacion() {
		return creacion;
	}
}
