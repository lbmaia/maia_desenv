package br.com.maia.servico;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.maia.entidade.Usuario;
import br.com.maia.util.PoolString;

/**
 * Session Bean implementation class UsuarioServiceBean
 */
@Stateless(mappedName = "UsuarioServiceBean")
@LocalBean
@Path("/usuario")
public class UsuarioServiceBean implements Serializable {
	
   /**
	 * 
	 */
   private static final long serialVersionUID = 664924304603977470L;
   @PersistenceContext(name="maia-ds")	
   private EntityManager session;

   /**
    * Metodo responsável por realizar o login do usuario
 * @param userName
 * @param password
 * @return IUsuario
 */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/login")
  public Usuario efetuarLogin(Usuario user){
	   
	   try {
		   Usuario usuario = null;
		   
		   Query query = session.createQuery(" from Usuario u where u.username = :username and u.password = :password "); 
		   query.setParameter("password", user.getPassword());
		   query.setParameter("username", user.getUsername());
		   
		   usuario  = (Usuario) query.getSingleResult();
		   		   
		   return usuario;
		} catch (Exception e) {
			return null;
		}
	   
   }
  
   /**
    * Metodo responsavel por incluir um usuario na base 
 * @param usuario
 */
  /* (non-Javadoc)
 * @see br.com.maia.interfaces.IUsuarioServiceBean#incluirUsuario(br.com.maia.dto.UsuarioDTO)
 */
   public void incluirUsuario(Usuario usuario){
	   
	   try{
		   
		   if(permiteInclusaoUserName(usuario)){
			   Usuario entUser = new Usuario();
			   entUser.setUsername(usuario.getUsername());
			   entUser.setPassword(usuario.getPassword());
			   entUser.setNomeUsuario(usuario.getNomeUsuario());
			   session.persist(entUser);  
		   }else{
			   throw new EJBException(PoolString.USERNAME_JA_EXISTE);   
		   }
		   
	   }catch(Exception e){
		   throw new EJBException(e);
	   }
	   
   }
  
   /**
    * Metodo responsavel por valida se o user name informado já existe
 * @param usuario
 * @return
 * @throws Exception 
 */
	private boolean permiteInclusaoUserName(Usuario usuario) throws Exception {
	
	    try{
	  	   
	       Query query = session.createQuery(" from Usuario u where u.username = :username "); 
		   query.setParameter("username", usuario.getUsername());
		   
		   query.getSingleResult();
		   
		   return Boolean.FALSE;
			   
	    }catch(NoResultException e){
	    	
	       return Boolean.TRUE;
	       
	    }catch(Exception e){
	    	
	    	throw e;
	    	
	    }
	    
		
	}

/**
    * Metodo responsavel por alterar um usuario na base de dados
 * @param usuario
 */
  public void alterarUsuario(Usuario usuario){
	   
	   try{
		   
		   Usuario entUser = session.find(Usuario.class, usuario.getId());
		   
		   if(entUser != null){
			   
			   if(usuario.getUsername().equalsIgnoreCase(entUser.getUsername()) || ( permiteInclusaoUserName(usuario))){
				   entUser.setUsername(usuario.getUsername());
				   entUser.setPassword(usuario.getPassword());
				   entUser.setNomeUsuario(usuario.getNomeUsuario());
				   session.merge(entUser);  
			   }else{
				   throw new EJBException(PoolString.USERNAME_JA_EXISTE);
			   }
			     
		   }else{
			   throw new Exception(PoolString.USUARIO_NAO_LOCALIZADO);
		   }
		   
	   }catch(Exception e){
		   throw new EJBException(e);
	   }
   }
  
  /**
   * Metodo responsavel por excluir um usuario
 * @param idUsuario
 */
   public void excluirUsuario(Integer idUsuario){
	   
	   try{
		   
		   Usuario entUser = session.find(Usuario.class, idUsuario);
		   
		   if(entUser != null){
			   session.remove(entUser);
		   }else{
			   throw new Exception(PoolString.USUARIO_NAO_LOCALIZADO);
		   }
		   
	   }catch(Exception e){
		   throw new EJBException(e);
	   }
  }
  
   /**
    * Metodo responsavel por retornar uma lista e usuarios
  * @return
  */
   @SuppressWarnings("unchecked")
   public List<Usuario> consultaUsuarios(String nomeUsuario){
	   
	   StringBuilder sb = new StringBuilder(" from Usuario u  ");
	   
	   if( nomeUsuario != null && !"".equals(nomeUsuario.trim())){
		   sb.append(" where u.nomeUsuario like :=nomeUsuario ");
	   }
	   
	   Query query = session.createQuery(sb.toString());
	   
	   if(nomeUsuario != null && !"".equals(nomeUsuario.trim())){
		   query.setParameter("nomeUsuario", "%"+nomeUsuario+"%");
	   }
	   
	   List<Usuario> usuarios = query.getResultList();
	   
	   
	   
	   return usuarios;
	   
   }

}
