/**
 * 
 */
package br.com.maia.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.maia.entidade.Usuario;
import br.com.maia.servico.UsuarioServiceBean;
import br.com.maia.util.PoolString;
import br.com.maia.util.Util;

/**
 * @author lmaia
 *
 */
@ManagedBean
@ViewScoped
public class LoginBean implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -9100648906051391184L;
	
	@EJB
	private UsuarioServiceBean usuarioService;
	
	private String username;
	
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String efetuarLogin(){
		
		Usuario usuario = new Usuario();;
		try {
			
			usuario.setUsername(username);
			usuario.setPassword(password);
			
			usuario = usuarioService.efetuarLogin(usuario);
			
			if(usuario == null){
				Util.addError(PoolString.USUARIO_OU_SENHA_INVÁLIDOS);
			}else{
				Util.addSessionAtribute(PoolString.USUARIO_VAR, usuario);
				return PoolString.DIRETIVA_MENU;
			}
		} catch (Exception e) {
			Util.addFatal(e.getMessage());
		}
		
		
		
		return null;
	}
	
	@PostConstruct
	private void init(){
	    if(Util.retrieveSessionAtribute(PoolString.USUARIO_VAR) != null){
	    	Util.removeSessionAtribute(PoolString.USUARIO_VAR);
	    }
	}
	
	public String retornaMenu(){
		
		Util.removeSessionAtribute(PoolString.CATEGORIA);
		Util.removeSessionAtribute(PoolString.DESPESA);
		return PoolString.DIRETIVA_MENU;
	}

}
