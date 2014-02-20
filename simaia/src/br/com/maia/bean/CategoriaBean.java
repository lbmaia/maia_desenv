package br.com.maia.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.maia.entidade.Categoria;
import br.com.maia.servico.CategoriaServiceBean;
import br.com.maia.util.PoolString;
import br.com.maia.util.Util;

@ManagedBean
@ViewScoped
public class CategoriaBean {
	
	private Categoria categoria;
	private List<Categoria> categorias;
	private boolean exibeInsert;
    private boolean exibeUpdate;
    private boolean exibeDelete;
    
    @EJB
    private CategoriaServiceBean categoriaService;
    
    @PostConstruct
    public void init(){
    	
    	categorias = new ArrayList<Categoria>();
    	
    	if(Util.retrieveSessionAtribute(PoolString.CATEGORIA) == null){
    		limpaForm();
    	}else{
    		categoria = (Categoria) Util.retrieveSessionAtribute(PoolString.CATEGORIA);
    		exibeDelete = Boolean.TRUE;
        	exibeInsert = Boolean.FALSE;
        	exibeUpdate = Boolean.TRUE;    		
    	}
    	
    	
    }
    
    /**
     * Metodo responsavel por alterar uma Categoria
     * @return
     */
    public String alterarCategoria(){
    	
    	try{
    		
    		if(categoria != null){
    			
    			if(Util.isBlankorNull(categoria.getNomeCategoria())){
    				Util.addWarn(PoolString.NOME_CATEGORIA_OBRG);
    			}else{
    				categoriaService.alterarCategoria(categoria);
        			Util.addInfo(PoolString.OPERACAO_SUCESSO);
        			limpaForm();
    			}
    			
    		}else{
    			Util.addWarn(PoolString.SELECIONE_CATEGORIA_ALTERAR);
    		}
    		
    	}catch(Exception e){
    	  Util.addError(e.getMessage());	
    	}
    	
    	return null;
    	
    }
    
    /**
     * Metodo responsavel por incluir uma Categoria
     * @return
     */
    public String incluirCategoria(){
    	
        try{
        	
        	if(Util.isBlankorNull(categoria.getNomeCategoria())){
				Util.addWarn(PoolString.NOME_CATEGORIA_OBRG);
			}else{
				categoriaService.incluirCategoria(categoria);
	    		Util.addInfo(PoolString.OPERACAO_SUCESSO); 
    			limpaForm();
			}
              
    	}catch(Exception e){
    	  Util.addError(e.getMessage());	
    	}
    	
        return null;
    	
    }
    
    /**
     * Metodo responsavel por excluir uma Categoria
     * @return
     */
    public String excluirCategoria(){
    	
        try{
    		
    		if(categoria != null && categoria.getId() != null && categoria.getId().intValue() > 0){
    			categoriaService.excluirCategoria(categoria.getId());
    			Util.addInfo(PoolString.OPERACAO_SUCESSO);
    			limpaForm();
    		}else{
    			Util.addWarn(PoolString.SELECIONE_CATEGORIA_EXCLUIR);
    		}
    		
    	}catch(Exception e){
    	  Util.addError(e.getMessage());	
    	}
    	
        return null;
    	
    }
    
    /**
     * Metodo responsavel por consultar uma Categoria ou uma lista de Categorias
     * @return
     */
    public String consultarCategoria(){
    	
        try{
    		setCategorias(categoriaService.consultaCategorias(categoria.getNomeCategoria()));
    		
    		if(getCategorias().isEmpty()){
    			Util.addInfo(PoolString.SEM_RESULTADO);
    		}
    		
    	}catch(Exception e){
    	  Util.addError(e.getMessage());	
    	}
    	
        
    	return null;
    	
    }
    
    private void limpaForm(){
    	
    	categoria = new Categoria();
    	setCategorias(new ArrayList<Categoria>());
    	Util.removeSessionAtribute(PoolString.CATEGORIA); 
    	exibeDelete = Boolean.FALSE;
    	exibeInsert = Boolean.TRUE;
    	exibeUpdate = Boolean.FALSE;
    	
    }
    
    public String selecionaCategoria(){
    	Util.addSessionAtribute(PoolString.CATEGORIA, categoria);
    	return PoolString.DIRETIVA_CAD_CATEGORIA;
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
	
	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	public boolean isListaVazia(){
		return this.getCategorias().isEmpty() ;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
	

}
