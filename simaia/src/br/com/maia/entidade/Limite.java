package br.com.maia.entidade;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="limitegastos")
public class Limite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3357959216428183111L;
    
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="idLimite")
	private Integer idLimite;
    
	@Column(name="valorAlerta", nullable=false)
	private BigDecimal limiteAlerta;
    
	@Column(name="valorMaximo" , nullable=false)
	private BigDecimal limiteMaximo;
    
	public Integer getIdLimite() {
		return idLimite;
	}
	
	public void setIdLimite(Integer idLimite) {
		this.idLimite = idLimite;
	}
	
	public BigDecimal getLimiteAlerta() {
		return limiteAlerta;
	}
	
	public void setLimiteAlerta(BigDecimal limiteAlerta) {
		this.limiteAlerta = limiteAlerta;
	}
	
	public BigDecimal getLimiteMaximo() {
		return limiteMaximo;
	}
	
	public void setLimiteMaximo(BigDecimal limiteMaximo) {
		this.limiteMaximo = limiteMaximo;
	}
	
	
	
}
