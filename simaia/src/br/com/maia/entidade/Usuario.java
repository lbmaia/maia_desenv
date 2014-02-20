/**
 * 
 */
package br.com.maia.entidade;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author lmaia
 *
 */
@Entity
@Table(name="usuario")
public class Usuario implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 9028397613739207342L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idUsuario")
	private Integer id;
	
	@Column(length=10, nullable=false, name="username")
	private String username;
	
	@Column(length=60, nullable=false, name="password")
	private String password;
	
	@Column(length=60, nullable=false, name="nomeUsuario")
	private String nomeUsuario;

	/* (non-Javadoc)
	 * @see br.com.maia.entidades.IUsuario#getId()
	 */
	
	public Integer getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see br.com.maia.entidades.IUsuario#setId(java.lang.Integer)
	 */
	
	public void setId(Integer id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see br.com.maia.entidades.IUsuario#getUsername()
	 */
	
	public String getUsername() {
		return username;
	}

	/* (non-Javadoc)
	 * @see br.com.maia.entidades.IUsuario#setUsername(java.lang.String)
	 */
	
	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see br.com.maia.entidades.IUsuario#getPassword()
	 */
	
	public String getPassword() {
		return password;
	}

	/* (non-Javadoc)
	 * @see br.com.maia.entidades.IUsuario#setPassword(java.lang.String)
	 */
	
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	public String toString() {
		return "Usuario [id=" + id + ", username=" + username + ", password="
				+ password + "]";
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	
	
	
}
