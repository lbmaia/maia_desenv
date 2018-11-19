package br.com.maia.entidade;

import java.math.BigDecimal;

import br.com.maia.enums.EMes;

public class GastoCategoria extends Gasto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5624855633364927044L;
	private String nomeCategoria;
	
	public GastoCategoria(String nomeCategoria, Integer ano, BigDecimal valorTotal, BigDecimal valorTotalRealidado) {
		super();
		this.nomeCategoria = nomeCategoria;
		super.setAno(ano);
		super.setValorTotal(valorTotal);
		super.setValorTotalRealizado(valorTotalRealidado);
	}
	
	public GastoCategoria(String nomeCategoria, Integer ano, int mes, BigDecimal valorTotal, BigDecimal valorTotalRealidado) {
		super();
		
		this.nomeCategoria = nomeCategoria;
		super.setAno(ano);
		super.setValorTotal(valorTotal);
		super.setValorTotalRealizado(valorTotalRealidado);
		super.setMes(EMes.recuperaMes(mes));
	}
	
	public GastoCategoria(String nomeCategoria,  BigDecimal valorTotal, BigDecimal valorTotalRealidado) {
		super();
		this.nomeCategoria = nomeCategoria;
		super.setValorTotal(valorTotal);
		super.setValorTotalRealizado(valorTotalRealidado);
	}

	public String getNomeCategoria() {
		return nomeCategoria;
	}
	
	public void setNomeCategoria(String nomeCategoria) {
		this.nomeCategoria = nomeCategoria;
	}

}
