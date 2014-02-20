/**
 * 
 */
package br.com.maia.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.maia.util.PoolString;
import br.com.maia.util.Util;

/**
 * @author lmaia
 *
 */
@ManagedBean
@ViewScoped
public class MenuBean implements Serializable {

	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2238025473881795303L;

	public String retornaMenu(){
		
		Util.removeSessionAtribute(PoolString.CATEGORIA);
		Util.removeSessionAtribute(PoolString.DESPESA);
		return PoolString.DIRETIVA_MENU;
	}

}
