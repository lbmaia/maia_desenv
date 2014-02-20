package br.com.maia.enums;

public enum ETipoImportacao {
	
	NAO_USAR(0,"Selecione uma opção"),
	PLANILHA_EXCEL(1,"Planilha de Excel"),
	IMAGEM(2,"Imagem digitalizada");	
	
	private Integer tipo;
	private String nome;
		
	private ETipoImportacao(Integer tipo, String nome){
		this.tipo = tipo;
		this.nome = nome;
	}
	
	public static ETipoImportacao retornaTipoRelatorio(int tipo){
		
		for(ETipoImportacao eTipoRelatorio : ETipoImportacao.values() ){
			
			if(eTipoRelatorio.getTipo().intValue() == tipo){
				return eTipoRelatorio;
			}
		}
		return null;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
