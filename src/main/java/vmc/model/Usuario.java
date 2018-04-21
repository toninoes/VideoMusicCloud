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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "usuarios")
public class Usuario  {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotBlank(message = "*Introduzca su nombre")
	private String nombre;
	
	@NotBlank(message = "*Introduzca sus apellidos")
	private String apellidos;
	
	@Column(unique = true)
	@Email(message = "*Introduzca un email válido")
	@NotBlank(message = "*Introduzca un email")
	private String mail;

	@Length(min = 6, message = "*Su contraseña debe contener al menos 6 caracteres")
	@NotBlank(message = "*Introduzca su contraseña de acceso")
	@Transient
	private String password;
	
	@NotBlank
	private String foto;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "siguiendo", joinColumns = @JoinColumn(name = "siguiendo_id"), inverseJoinColumns = @JoinColumn(name = "seguido_id"))
	private Set<Usuario> siguiendo;

	/*@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "seguidores", joinColumns = @JoinColumn(name = "seguido_id"), inverseJoinColumns = @JoinColumn(name = "siguiendo_id")) ????
	private Set<Usuario> seguidores;*/
	
	private boolean activo;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "usuario_rol", 
			   joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"), 
			   inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "id"))
	private Set<Rol> roles;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date creacion;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date actualizacion;

	public Usuario() {
		super();
		this.foto = "img/ava_10.jpg";
	}
	
	public Usuario(@NotBlank String nombre, @NotBlank String apellidos, @NotBlank String mail, 
			       @NotBlank String password, @NotBlank String foto) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.mail = mail;
		this.password = password;
		this.foto = foto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public boolean getActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}
	
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public String getFoto() {
		return foto;
	}

	public void setSiguiendos(Set<Usuario> siguiendo) {
		this.siguiendo = siguiendo;
	}
	
	public void setSiguiendo(Usuario usuario) {
		this.siguiendo.add(usuario);
	}
	
	public void removeSiguiendo(Usuario usuario) {
		this.siguiendo.remove(usuario);
	}
	
	public Set<Usuario> getSiguiendo() {
		return siguiendo;
	}
}