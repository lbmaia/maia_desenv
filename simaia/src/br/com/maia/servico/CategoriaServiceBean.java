package br.com.maia.servico;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.maia.entidade.Categoria;
import br.com.maia.util.PoolString;

/**
 * Session Bean implementation class CategoriaServiceBean
 */
@Stateless(mappedName = "CategoriaServiceBean")
@LocalBean
@Path("/categoria")
public class CategoriaServiceBean implements Serializable {
	
   /**
	 * 
	 */
   private static final long serialVersionUID = -5813011592221559338L;
   @PersistenceContext(name="maia-ds")	
   private EntityManager session;
   
   @EJB
   private DespesaServiceBean despesaService;
   
   /**
    * Metodo responsavel por incluir um Categoria na base 
 * @param Categoria
 */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/manter")
  public void incluirCategoria(Categoria categoria){
	   
	   try{
		   
		   if(permiteInclusaoCategoria(categoria)){
			   session.persist(categoria);
		   }else{
			   throw new EJBException(PoolString.CATEGORIA_JA_CADASTRADA);
		   }
		   
	   }catch(Exception e){
		   throw new EJBException(e);
	   }
	   
   }
  
   /**
    * Metodo responsavel por alterar uma Categoria na base de dados
 * @param categoria
 */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/manter")
  public void alterarCategoria(Categoria categoria){
	   
	   try{
		   
	     session.merge(categoria);
		   		   
	   }catch(Exception e){
		   throw new EJBException(e);
	   }
   }
  
  /**
   * Metodo responsavel por excluir uma Categoria
 * @param idCategoria
 */
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/manter/{categoria}")
   public void excluirCategoria(@PathParam("categoria")Integer idCategoria){
	   
	   try{
		   
		   boolean exclui = despesaService.despesaCategoria(idCategoria);
		   
		   if(!exclui){
			   throw new Exception(PoolString.CATEGORIA_COM_DESPESAS);
		   }
		   
		   Categoria entCategoria = session.find(Categoria.class, idCategoria);
		   
		   if(entCategoria != null){
			   session.remove(entCategoria);
		   }else{
			   throw new Exception(PoolString.CATEGORIA_NAO_LOCALIZADA);
		   }
		   
	   }catch(Exception e){
		   throw new EJBException(e);
	   }
  }
  
   /**
    * Metodo responsavel por retornar uma lista e Categorias
  * @return
  */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/consultar/{categoria}")
   @SuppressWarnings("unchecked")
   public List<Categoria> consultaCategorias(@PathParam("categoria")String nomeCategoria){
	   
	   StringBuilder sb = new StringBuilder(" from Categoria u  ");
	   
	   if( nomeCategoria != null && !"".equals(nomeCategoria.trim()) && !"all".equals(nomeCategoria.trim())){
		   sb.append(" where u.nomeCategoria like :nomeCategoria ");
	   }
	   
	   sb.append(" order by u.nomeCategoria ");
	   
	   Query query = session.createQuery(sb.toString());
	   
	   if(nomeCategoria != null && !"".equals(nomeCategoria.trim()) && !"all".equals(nomeCategoria.trim())){
		   query.setParameter("nomeCategoria", "%"+nomeCategoria+"%");
	   }
	   
	   
	   
	   List<Categoria> categorias = query.getResultList();
	   
	   
	   return categorias;
	   
   }
   
   /**
    * Metodo responsavel por valida se a categoria informada já existe
 * @param categoria
 * @return
 * @throws Exception 
 */
	private boolean permiteInclusaoCategoria(Categoria categoriaDTO) throws Exception {
	
	    try{
	  	   
	       Query query = session.createQuery(" from Categoria u where u.nomeCategoria = :nomeCategoria "); 
		   query.setParameter("nomeCategoria", categoriaDTO.getNomeCategoria());
		   
		   query.getSingleResult();
		   
		   return Boolean.FALSE;
			   
	    }catch(NoResultException e){
	    	
	       return Boolean.TRUE;
	       
	    }catch(Exception e){
	    	
	    	throw e;
	    	
	    }
	    
		
	}	

}
