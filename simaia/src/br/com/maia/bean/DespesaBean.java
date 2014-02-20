package br.com.maia.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import br.com.maia.datamodel.ParcelaDataModel;
import br.com.maia.entidade.Categoria;
import br.com.maia.entidade.Despesa;
import br.com.maia.entidade.Parcela;
import br.com.maia.servico.CategoriaServiceBean;
import br.com.maia.servico.DespesaServiceBean;
import br.com.maia.util.PoolString;
import br.com.maia.util.Util;

@ManagedBean
@ViewScoped
public class DespesaBean implements Serializable {
	
	private static final String NENHUMA_DESPESA_FOI_SELECIONADA_PARA_PAGAMENTO = "Nenhuma Despesa foi selecionada para pagamento!";

	/**
	 * 
	 */
	private static final long serialVersionUID = 4113680742524030901L;
	
	@EJB
	private DespesaServiceBean despesaService;
	
	@EJB
	private CategoriaServiceBean categoriaService;
	
	private Despesa despesa;
	private List<Parcela> parcelas;
	private ParcelaDataModel parcelasModel; 
	private List<Categoria> categorias;
	private Parcela parcela;
	private Integer numParcelas;
	private Date dataVencimento;
	private boolean exibeDados;
	private BigDecimal valor;
	private boolean exibeInsert;
	private boolean exibeUpdate;
	private boolean exibeDelete;
	private String nomeDespesa;
	private Integer mes;
	private Integer ano;
	private Integer idCategoria;
	private Parcela[] selectedParcelas;
	private boolean exibeBtPagar;
	
	
	@PostConstruct
	public void init(){
		
		if(Util.retrieveSessionAtribute(PoolString.DESPESA) != null){
			despesa = (Despesa) Util.retrieveSessionAtribute(PoolString.DESPESA);
			parcelas = despesa.getParcelas();
			exibeDados = Boolean.FALSE;
			exibeInsert = Boolean.FALSE;
			exibeUpdate = Boolean.TRUE;
			exibeDelete = Boolean.TRUE;
			exibeBtPagar = Boolean.TRUE;
		}else{
			despesa = new Despesa();
			Categoria categoria = new Categoria();
			despesa.setCategoria(categoria);
			parcelas = new ArrayList<Parcela>();
			exibeDados = Boolean.TRUE;
			exibeInsert = Boolean.TRUE;
			exibeUpdate = Boolean.FALSE;
			exibeDelete = Boolean.FALSE;
			exibeBtPagar = Boolean.FALSE;
			Calendar calendar = Calendar.getInstance();
			ano = calendar.get(Calendar.YEAR);
			mes = calendar.get(Calendar.MONTH)+1;
			
		}
		
		nomeDespesa = "";
		idCategoria = 0;
		
		setNumParcelas(new Integer(0));
		categorias = categoriaService.consultaCategorias(null);
		parcelasModel = new ParcelaDataModel(parcelas);		
	}
	
	public Despesa getDespesa() {
		return despesa;
	}
	
	public void setDespesa(Despesa despesa) {
		this.despesa = despesa;
	}
	
	public List<Parcela> getParcelas() {
		return parcelas;
	}
	
	public void setParcelas(List<Parcela> parcelas) {
		this.parcelas = parcelas;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public Parcela getParcela() {
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}
	
	public String excluirParcela(){
		
        Integer numParcela = parcela.getNumParcela();
    	
    	parcelas.remove(parcela);
    	
    	for(Parcela parcela : parcelas){
    		
    		if(parcela.getNumParcela() > numParcela){
    			parcela.setNumParcela(parcela.getNumParcela()-1);
    		}
    		
    		parcela.setQtdParcelas(parcela.getQtdParcelas()-1);
    	}
    	
    	parcela = new Parcela();
    	
		return null;
	}
	
	public String editarDespesa(){
		
		Util.addSessionAtribute(PoolString.DESPESA, parcela.getDespesa());
		
		
		
		return PoolString.DIRETIVA_CAD_DESPESA;
	}
	
	public String incluirParcelas(){
		
		boolean possuiErro = Boolean.FALSE;
		
		if(Util.isBlankorNull(despesa.getDescricao())){
			
			Util.addInfo(PoolString.DESCRCICAO_OBRIG);
			possuiErro = Boolean.TRUE;			
		}
		
		if(Util.isBlankorNull(despesa.getCategoria().getId())){
			Util.addInfo(PoolString.CATEGORIA_OBRG);
			possuiErro = Boolean.TRUE;
		}
		
		
		if(exibeDados && (dataVencimento == null || dataVencimento.before(new Date()))){
			Util.addInfo(PoolString.DATA_VENCIMENTO_OBRIG);
			possuiErro = Boolean.TRUE;
		}
		
        if(exibeDados && (valor == null || valor.compareTo(BigDecimal.ZERO) < 1)){
        	Util.addInfo(PoolString.VALOR_OBRIG);
			possuiErro = Boolean.TRUE;
		}
        
        if(Util.isBlankorNull(numParcelas) || numParcelas < 1){
        	Util.addInfo(PoolString.PARCELA_OBRIG);
			possuiErro = Boolean.TRUE;
        }
		
        if(possuiErro){
        	return null;
        }
        
        Calendar dataVenc = Calendar.getInstance();
        int ultimaParcela = 0;
        int totalParcela = 0;
        
		if(!parcelas.isEmpty()){
			
			setDataVencimento(parcelas.get(parcelas.size()-1).getDtVencimento());
			setValor(parcelas.get(parcelas.size()-1).getValor());
			ultimaParcela = parcelas.get(parcelas.size()-1).getNumParcela();
			totalParcela = parcelas.get(parcelas.size()-1).getQtdParcelas();
		}
		
		dataVenc.setTime(dataVencimento);
		totalParcela += getNumParcelas();
		
		if(!exibeDados){
			dataVenc.add(Calendar.DAY_OF_MONTH, 30);
		}
		
		for(int i = 0; i < getNumParcelas(); i++){
			
			ultimaParcela++;
			
			Date dataParcela = dataVenc.getTime();
			
			Parcela parcelaNova = new Parcela();
			parcelaNova.setAnoDespesa(dataVenc.get(Calendar.YEAR));
			parcelaNova.setMesDespesa(dataVenc.get(Calendar.MONTH)+1);
			parcelaNova.setDespesa(despesa);
			parcelaNova.setDtVencimento(dataParcela);
			parcelaNova.setNumParcela(ultimaParcela);
			parcelaNova.setQtdParcelas(totalParcela);
			parcelaNova.setValor(valor);
			parcelaNova.setValorPago(BigDecimal.ZERO);
			parcelas.add(parcelaNova);
			
			dataVenc.add(Calendar.DAY_OF_MONTH, 30);
			
		}
		
		return null;
		
	}
	
	public void onEdit(RowEditEvent event) {  

	}  
      
    public void onCancel(RowEditEvent event) {  
    
    }  
    
    /**
     * Metodo responsavel por alterar uma Despesa
     * @return
     */
    public String alterarDespesa(){
    	
    	try{
        	if(Util.isBlankorNull(despesa.getDescricao())){
        		Util.addError(PoolString.DESCRCICAO_OBRIG);
        	}else if(Util.isBlankorNull(despesa.getCategoria().getId())){
        		Util.addError(PoolString.CATEGORIA_OBRG);
        	}else if(parcelas.isEmpty()){
        		Util.addError(PoolString.PARCELA_OBRIG);
        	}else{
        		
        		despesa.setParcelas(getParcelas());
        		
        		despesaService.alterarDespesa(despesa);
            	Util.addInfo(PoolString.DESPESA_ALTERADA_SUCESSO);
        	}
        	
        }catch(Exception e){
        	Util.addError(e.getMessage());
        }
    	
    	return null;
    	
    }
    
    /**
     * Metodo responsavel por incluir uma Despesa
     * @return
     */
    public String incluirDespesa(){
    	
        try{
        	if(Util.isBlankorNull(despesa.getDescricao())){
        		Util.addError(PoolString.DESCRCICAO_OBRIG);
        	}else if(Util.isBlankorNull(despesa.getCategoria().getId())){
        		Util.addError(PoolString.CATEGORIA_OBRG);
        	}else if(parcelas.isEmpty()){
        		Util.addError(PoolString.PARCELA_OBRIG);
        	}else{
        		despesa.setParcelas(parcelas);
        		despesaService.incluirDespesa(despesa);
        		limpaForm();
            	Util.addInfo(PoolString.DESPESA_INCLUIDA_SUCESSO);
        	}
        	
        }catch(Exception e){
        	Util.addError(e.getMessage());
        }
    	
    	return null;
    	
    }
    
    /**
     * Metodo responsavel por excluir uma Despesa
     * @return
     */
    public String excluirDespesa(){
    	
    	try{
        	
    		if(despesa == null || (despesa.getId().intValue() <= 0 )){
        		Util.addError(PoolString.DESPESA_EXC_OBRIG);
        	}else{
        		
        		despesaService.excluirDespesa(despesa.getId());
        		limpaForm();
            	Util.addInfo(PoolString.DESPESA_EXCLUIDA_SUCESSO);
        	}
        	
        }catch(Exception e){
        	Util.addError(e.getMessage());
        }
        
    	return null;
    	
    }
    
    /**
     * Metodo responsavel por consultar uma Despesa ou uma lista de Despesas
     * @return
     */
    public String consultarDespesa(){
    
    	limpaForm();
    	
        try{
        	
        	if( !Util.isBlankorNull(mes) && !(mes > 0  && mes <=12)){
        		Util.addInfo(PoolString.MES_INVALIDO);
        		return null;
        	}
        	
        	if( !Util.isBlankorNull(ano) && (ano < 2000)){
        		Util.addInfo(PoolString.ANO_INVALIDO);
        		return null;
        	}
        	
    		parcelas = despesaService.consultaDespesas(nomeDespesa, idCategoria, mes, ano);
    		parcelasModel = new ParcelaDataModel(parcelas);
    		if(isListaVazia()){
    			Util.addInfo(PoolString.SEM_RESULTADO);
    		}else{
    			exibeBtPagar = Boolean.TRUE;
    		}
    		
    	}catch(Exception e){
    	  Util.addError(e.getMessage());	
    	}
    	
        
    	return null;
    	
    }
    
    /** 
     * Metodo responsavel por efetuar pagamento de despesas
     * @return
     */
    public String pagarDespesa(){
        
    	limpaForm();
    	
        try{
        	
        	if( selectedParcelas != null && selectedParcelas.length <=0){
        		Util.addInfo(NENHUMA_DESPESA_FOI_SELECIONADA_PARA_PAGAMENTO);
        		return null;
        	}
        	
        	for(Parcela parcela : selectedParcelas){
        		parcela.setDtPagamento(new Date());
        		parcela.setValorPago(parcela.getValor());
        		despesaService.alterarParcela(parcela);
        	}
    		
    		selectedParcelas = null;
    		parcelasModel = new ParcelaDataModel(new ArrayList<Parcela>());
    		Util.addInfo("Pagamentos realizados com sucesso!");
    		
    	}catch(Exception e){
    	  Util.addError(e.getMessage());	
    	}
    	
        
    	return null;
    	
    }
    
    public boolean isListaVazia() {
		
		return parcelas.isEmpty();
	}

	private void limpaForm(){
    	
    	despesa = new Despesa();
		Categoria categoria = new Categoria();
		despesa.setCategoria(categoria);
		parcelas = new ArrayList<Parcela>();
		exibeDados = Boolean.TRUE;
		exibeInsert = Boolean.TRUE;
		exibeUpdate = Boolean.FALSE;
		exibeDelete = Boolean.FALSE;
		exibeBtPagar = Boolean.FALSE;
    }

	public Integer getNumParcelas() {
		return numParcelas;
	}

	public void setNumParcelas(Integer numParcelas) {
		this.numParcelas = numParcelas;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}


	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public boolean isExibeDados() {
		return exibeDados;
	}

	public void setExibeDados(boolean exibeDados) {
		this.exibeDados = exibeDados;
	}

	public boolean isExibeInsert() {
		return exibeInsert;
	}

	public void setExibeInsert(boolean exibeInsert) {
		this.exibeInsert = exibeInsert;
	}

	public boolean isExibeUpdate() {
		return exibeUpdate;
	}

	public void setExibeUpdate(boolean exibeUpdate) {
		this.exibeUpdate = exibeUpdate;
	}

	public boolean isExibeDelete() {
		return exibeDelete;
	}

	public void setExibeDelete(boolean exibeDelete) {
		this.exibeDelete = exibeDelete;
	}

	public String getNomeDespesa() {
		return nomeDespesa;
	}

	public void setNomeDespesa(String nomeDespesa) {
		this.nomeDespesa = nomeDespesa;
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

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public Parcela[] getSelectedParcelas() {
		return selectedParcelas;
	}

	public void setSelectedParcelas(Parcela[] selectedParcelas) {
		this.selectedParcelas = selectedParcelas;
	}

	public ParcelaDataModel getParcelasModel() {
		return parcelasModel;
	}

	public void setParcelasModel(ParcelaDataModel parcelasModel) {
		this.parcelasModel = parcelasModel;
	}

	public boolean isExibeBtPagar() {
		return exibeBtPagar;
	}

	public void setExibeBtPagar(boolean exibeBtPagar) {
		this.exibeBtPagar = exibeBtPagar;
	}
	

}
