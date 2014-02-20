package br.com.maia.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.maia.entidade.Usuario;
import br.com.maia.servico.UsuarioServiceBean;
import br.com.maia.util.PoolString;
import br.com.maia.util.Util;

@ManagedBean
@ViewScoped
public class UsuarioBean {
	
	  private Usuario usuario;
	  private List<Usuario> usuarios;
	  private boolean exibeInsert;
	  private boolean exibeUpdate;
	  private boolean exibeDelete;
	  
	  @EJB
      private UsuarioServiceBean usuarioService;	    
	    
	    /**
	     * Metodo responsavel por alterar um usuario
	     * @return
	     */
	    public String alterarUsuario(){
	    	
	    	try{
	    		
	    		if(getUsuario() != null){
	    			
	    			if(Util.isBlankorNull(getUsuario().getNomeUsuario())){
	    				Util.addWarn(PoolString.NOME_USUARIO_OBRG);
	    			}else if(Util.isBlankorNull(getUsuario().getPassword())){
	    				Util.addWarn(PoolString.PASSWORD_OBRG);
	    			}else if(Util.isBlankorNull(getUsuario().getUsername())){
	    				Util.addWarn(PoolString.USER_NAME_OBRG);
	    			}else{
	    				usuarioService.alterarUsuario(getUsuario());
	        			Util.addInfo(PoolString.OPERACAO_SUCESSO);
	        			limpaForm();
	    			}
	    			
	    		}else{
	    			Util.addWarn(PoolString.SELECIONE_USUARIO_ALTERAR);
	    		}
	    		
	    	}catch(Exception e){
	    	  Util.addError(e.getMessage());	
	    	}
	    	
	    	return null;
	    	
	    }
	    
	    /**
	     * Metodo responsavel por incluir um usuario
	     * @return
	     */
	    public String incluirUsuario(){
	    	
	        try{
	        	
	        	if(Util.isBlankorNull(getUsuario().getNomeUsuario())){
					Util.addWarn(PoolString.NOME_USUARIO_OBRG);
				}else if(Util.isBlankorNull(getUsuario().getPassword())){
					Util.addWarn(PoolString.PASSWORD_OBRG);
				}else if(Util.isBlankorNull(getUsuario().getUsername())){
					Util.addWarn(PoolString.USER_NAME_OBRG);
				}else{
					usuarioService.incluirUsuario(getUsuario());
		    		Util.addInfo(PoolString.OPERACAO_SUCESSO); 
	    			limpaForm();
				}
	              
	    	}catch(Exception e){
	    	  Util.addError(e.getMessage());	
	    	}
	    	
	        return null;
	    	
	    }
	    
	    /**
	     * Metodo responsavel por excluir um usuario
	     * @return
	     */
	    public String excluirUsuario(){
	    	
	        try{
	    		
	    		if(getUsuario() != null && getUsuario().getId() != null && getUsuario().getId().intValue() > 0){
	    			usuarioService.excluirUsuario(getUsuario().getId());
	    			Util.addInfo(PoolString.OPERACAO_SUCESSO);
	    			limpaForm();
	    		}else{
	    			Util.addWarn(PoolString.SELECIONE_USUARIO_EXCLUIR);
	    		}
	    		
	    	}catch(Exception e){
	    	  Util.addError(e.getMessage());	
	    	}
	    	
	        return null;
	    	
	    }
	    
	    /**
	     * Metodo responsavel por consultar um usuario ou uma lista de usuarios
	     * @return
	     */
	    public String consultarUsuario(){
	    	
	    	try{
	    		setUsuarios(usuarioService.consultaUsuarios(getUsuario().getNomeUsuario()));
	    		
	    		if(isListaVazia()){
	    			Util.addInfo(PoolString.SEM_RESULTADO);
	    		}
	    		
	    	}catch(Exception e){
	    	  Util.addError(e.getMessage());	
	    	}
	    	
	        
	    	return null;
	    	
	    }
	    
	    private void limpaForm(){
	    	
	    	setUsuario(new Usuario());
	    	setUsuarios(new ArrayList<Usuario>()); 
	    	exibeDelete = Boolean.FALSE;
	    	exibeInsert = Boolean.TRUE;
	    	exibeUpdate = Boolean.FALSE;
	    	
	    }
	    
	    public String selecionaUsuario(){
	    	
	    	Util.addSessionAtribute(PoolString.USUARIO, getUsuario());
	    	return PoolString.DIRETIVA_CAD_USUARIO;
	    }
	    
		@PostConstruct
		private void init(){
			
			usuarios = new ArrayList<Usuario>();
			
			if(Util.retrieveSessionAtribute(PoolString.USUARIO) == null){
				limpaForm();
			}else{
				setUsuario((Usuario) Util.retrieveSessionAtribute(PoolString.USUARIO));
				exibeDelete = Boolean.TRUE;
		    	exibeInsert = Boolean.FALSE;
		    	exibeUpdate = Boolean.TRUE;
			}
			
			
			
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
		
		public boolean isListaVazia(){
			return this.getUsuarios().isEmpty();
		}

		public Usuario getUsuario() {
			return usuario;
		}

		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}

		public List<Usuario> getUsuarios() {
			return usuarios;
		}

		public void setUsuarios(List<Usuario> usuarios) {
			this.usuarios = usuarios;
		}

		
}
