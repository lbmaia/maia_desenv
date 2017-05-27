package br.com.maia.servico.parametros;

public class ConsultaDespesasParameter {
	private String descricao;
	private Integer idCategoria;
	private Integer mes;
	private Integer ano;

	public ConsultaDespesasParameter(String descricao, Integer idCategoria, Integer mes, Integer ano) {
		this.descricao = descricao;
		this.idCategoria = idCategoria;
		this.mes = mes;
		this.ano = ano;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}
}