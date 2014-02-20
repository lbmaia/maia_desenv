package br.com.maia.enums;

public enum ETipoRelatorio {
	
	NAO_USAR(0,"Selecione uma opção"),
	GASTO_ANUAL(1,"Gasto Consolidado Anual"),
	GASTO_MENSAL(2,"Gasto Consolidado Mensal"),
	GASTO_ANUAL_CATEGORIA(3, "Gasto Consolidado Anual por Categoria"),
	GASTO_MENSAL_CATEGORIA(4, "Gasto Consolidado Mensal por Categoria"),
	GASTO_PERIODO(5, "Gasto por Periodo"),
	GASTO_CATEGORIA_PERIODO(6, "Gasto por Periodo e Categoria");
	
	private Integer tipoRelatorio;
	private String nomeRelatorio;
		
	private ETipoRelatorio(Integer tipoRelatorio, String nomeRelatorio){
		this.tipoRelatorio = tipoRelatorio;
		this.nomeRelatorio = nomeRelatorio;
	}
	
	public static ETipoRelatorio retornaTipoRelatorio(int tipoRelatorio){
		
		for(ETipoRelatorio eTipoRelatorio : ETipoRelatorio.values() ){
			
			if(eTipoRelatorio.getTipoRelatorio().intValue() == tipoRelatorio){
				return eTipoRelatorio;
			}
		}
		return null;
	}

	public Integer getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(Integer tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

}
