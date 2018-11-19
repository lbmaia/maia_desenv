package br.com.maia.entidade;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.maia.enums.EMes;

public class Gasto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458420518151507454L;
	private Integer ano;
	private EMes mes;
	private BigDecimal valorTotal;
	private BigDecimal valorTotalRealizado;
	
	public Gasto(){
		//DEFAULT
	}
	
	
	
	public Gasto(Integer ano, BigDecimal valorTotal, BigDecimal valorTotalRealidado) {
		super();
		this.ano = ano;
		this.valorTotal = valorTotal;
		this.valorTotalRealizado = valorTotalRealidado;
	}
	
	public Gasto(Integer ano, int mes, BigDecimal valorTotal, BigDecimal valorTotalRealidado) {
		super();
		this.ano = ano;
		this.mes = EMes.recuperaMes(mes);
		this.valorTotal = valorTotal;
		this.valorTotalRealizado = valorTotalRealidado;
	}


	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public EMes getMes() {
		return mes;
	}
	public void setMes(EMes mes) {
		this.mes = mes;
	}



	public BigDecimal getValorTotal() {
		return valorTotal;
	}



	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}



	public BigDecimal getValorTotalRealizado() {
		return valorTotalRealizado;
	}



	public void setValorTotalRealizado(BigDecimal valorTotalRealizado) {
		this.valorTotalRealizado = valorTotalRealizado;
	}
	

}
