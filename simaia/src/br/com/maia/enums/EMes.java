package br.com.maia.enums;

public enum EMes {
	
	JANEIRO("Janeiro",1),
	FEVEREIRO("Fevereiro", 2),
	MARCO("Março", 3),
	ABRIL("Abril", 4),
	MAIO("Maio", 5),
	JUNHO("Junho", 6),
	JULHO("Julho", 7),
	AGOSTO("Agosto", 8),
	SETEMBRO("Setembro", 9),
	OUTUBRO("Outubro", 10),
	NOVEMBRO("Novembro", 11),
	DEZEMBRO("Dezembro", 12);
	
	
	private String nome;
	private Integer mes;
	
	EMes(String nome, Integer mes) {
		this.setNome(nome);
		this.setMes(mes);
	}
	
	public static EMes recuperaMes(int mes){
		
		for(EMes emes : EMes.values()){
			
			if(emes.getMes().intValue() == mes){
				return emes;
			}
		}
		
		return null;
		
	}
	
    public static EMes recuperaMesPorNome(String nomeMes){
		
		for(EMes emes : EMes.values()){
			
			if(emes.getNome().equalsIgnoreCase(nomeMes)){
				return emes;
			}
		}
		
		return null;
		
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}
	
	

}
