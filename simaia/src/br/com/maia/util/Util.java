package br.com.maia.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class Util {

	public static void addInfo(String msg) {  
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,msg, null));  
    }  
  
    public static void addWarn(String msg) {  
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,msg, null));  
    }  
  
    public static void addError(String msg) {  
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,msg, null));  
    }  
  
    public static void addFatal(String msg) {  
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,msg, null));  
    } 
    
    public static void addSessionAtribute(String atribName, Object atrib ){
    	
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext ();
    	HttpSession session = ( HttpSession )ec. getSession ( false );
    	session . setAttribute (atribName, atrib );
    }
    
    public static Object retrieveSessionAtribute(String atribName){
    	
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext ();
    	HttpSession session = ( HttpSession )ec. getSession ( false );
    	return session == null ? null : session . getAttribute (atribName);
    }
    
    public static void removeSessionAtribute(String atribName){
    	
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext ();
    	HttpSession session = ( HttpSession )ec. getSession ( false );
    	session . removeAttribute (atribName);
    }
    
    public static boolean isBlankorNull(String str){
    	
    	if(str == null || "".equalsIgnoreCase(str.trim())  ){
    		return Boolean.TRUE;
    	}else{
    		return Boolean.FALSE;
    	}
    	
    }
    
    public static boolean isBlankorNull(Integer str){
    	
    	if(str == null || str.intValue() == 0) {
    		return Boolean.TRUE;
    	}else{
    		return Boolean.FALSE;
    	}
    	
    }
	
}
