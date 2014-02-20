package br.com.maia.entidade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="parcela")
public class Parcela implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2194581544451339411L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idParcela")
	private Integer idParcela;
	
	@ManyToOne(targetEntity=Despesa.class, fetch=FetchType.EAGER)
	@JoinColumn(name="idDespesa")
	private Despesa despesa;
	
	@Column(name="valor", nullable=false )
	private BigDecimal valor;
	
	@Column(name="valorPago", nullable=false)
	private BigDecimal valorPago;
	
	@Column(name="dtVencimento", nullable=false)
	private Date dtVencimento;
	
	@Column(name="dtPagamento", nullable=true)
	private Date dtPagamento;
	
	@Column(name="numParcela", nullable=false)
	private Integer numParcela;
	
	@Column(name="qtdParcelas", nullable=false)
	private Integer qtdParcelas;
	
	@Column(name="mesDespesa", nullable=false)
	private Integer mesDespesa;
	
	@Column(name="anoDespesa", nullable=false)
	private Integer anoDespesa;

	public Integer getIdParcela() {
		return idParcela;
	}

	public void setIdParcela(Integer idParcela) {
		this.idParcela = idParcela;
	}

	public Despesa getDespesa() {
		return despesa;
	}

	public void setDespesa(Despesa despesa) {
		this.despesa = despesa;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getValorPago() {
		return valorPago;
	}

	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}

	public Date getDtVencimento() {
		return dtVencimento;
	}
	
	public Date getDataVencimento() {
		return dtVencimento;
	}

	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}

	public Date getDtPagamento() {
		return dtPagamento;
	}
	
	public Date getDataPagamento() {
		return dtPagamento;
	}

	public void setDtPagamento(Date dtPagamento) {
		this.dtPagamento = dtPagamento;
	}

	public Integer getNumParcela() {
		return numParcela;
	}

	public void setNumParcela(Integer numParcela) {
		this.numParcela = numParcela;
	}

	public Integer getQtdParcelas() {
		return qtdParcelas;
	}

	public void setQtdParcelas(Integer qtdParcelas) {
		this.qtdParcelas = qtdParcelas;
	}

	public Integer getMesDespesa() {
		return mesDespesa;
	}

	public void setMesDespesa(Integer mesDespesa) {
		this.mesDespesa = mesDespesa;
	}

	public Integer getAnoDespesa() {
		return anoDespesa;
	}

	public void setAnoDespesa(Integer anoDespesa) {
		this.anoDespesa = anoDespesa;
	}
	
	public String getDescricao(){
		return despesa.getDescricao();
	}
	
	public boolean isPago(){
		return dtPagamento != null && valorPago != null ? Boolean.TRUE : Boolean.FALSE;
				
	}

}
