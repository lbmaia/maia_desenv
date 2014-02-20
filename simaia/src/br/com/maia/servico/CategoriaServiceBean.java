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

import br.com.maia.entidade.Categoria;
import br.com.maia.util.PoolString;

/**
 * Session Bean implementation class CategoriaServiceBean
 */
@Stateless(mappedName = "CategoriaServiceBean")
@LocalBean
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
  public void incluirCategoria(Categoria categoria){
	   
	   try{
		   
		   if(permiteInclusaoCategoria(categoria)){
			   Categoria entCategoria = new Categoria();
			   entCategoria.setNomeCategoria(categoria.getNomeCategoria());
			   session.persist(entCategoria);  
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
  public void alterarCategoria(Categoria categoria){
	   
	   try{
		   
		   Categoria entCategoria = session.find(Categoria.class, categoria.getId());
		   
		   if(entCategoria != null){
			   
			   if(permiteInclusaoCategoria(categoria)){
				   entCategoria.setNomeCategoria(categoria.getNomeCategoria());
				   session.merge(entCategoria);
			   }else{
				   throw new EJBException(PoolString.CATEGORIA_JA_CADASTRADA);
			   }
			   
			     
		   }else{
			   throw new Exception(PoolString.CATEGORIA_NAO_LOCALIZADA);
		   }
		   
	   }catch(Exception e){
		   throw new EJBException(e);
	   }
   }
  
  /**
   * Metodo responsavel por excluir uma Categoria
 * @param idCategoria
 */
   public void excluirCategoria(Integer idCategoria){
	   
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
   @SuppressWarnings("unchecked")
   public List<Categoria> consultaCategorias(String nomeCategoria){
	   
	   StringBuilder sb = new StringBuilder(" from Categoria u  ");
	   
	   if( nomeCategoria != null && !"".equals(nomeCategoria.trim())){
		   sb.append(" where u.nomeCategoria like :nomeCategoria ");
	   }
	   
	   sb.append(" order by u.nomeCategoria ");
	   
	   Query query = session.createQuery(sb.toString());
	   
	   if(nomeCategoria != null && !"".equals(nomeCategoria.trim())){
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
