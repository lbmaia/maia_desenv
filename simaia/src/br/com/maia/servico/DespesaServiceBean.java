package br.com.maia.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.maia.entidade.Despesa;
import br.com.maia.entidade.Gasto;
import br.com.maia.entidade.GastoCategoria;
import br.com.maia.entidade.Limite;
import br.com.maia.entidade.Parcela;
import br.com.maia.servico.parametros.ConsultaDespesasParameter;
import br.com.maia.util.PoolString;

@Stateless(mappedName="DespesaServiceBean")
@Path("/despesa")
@LocalBean
public class DespesaServiceBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1360853253497856474L;
	@PersistenceContext(name="maia-ds")	
	private EntityManager session;
	
	
	/**
	 * Metodo responsavel por consultar despesas por descricao
	 * @param parameterObject TODO
	 * @return List<DespesaDTO>
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultar")
	@SuppressWarnings("unchecked")
	public List<Parcela> consultaDespesas(ConsultaDespesasParameter parameterObject){
		
		try{
			
			boolean temWhere = Boolean.FALSE;
			
			StringBuilder sb = new StringBuilder("from Parcela p ");
			
			if(parameterObject.getDescricao() != null && !"".equals(parameterObject.getDescricao().trim())){
				sb.append(" where p.despesa.descricao like :descricao ");
				temWhere = Boolean.TRUE;
			}
			
			if(parameterObject.getIdCategoria() != null && parameterObject.getIdCategoria().intValue() > 0){
				
				if(temWhere){
					sb.append(" and ");
				}else{
					sb.append(" where ");
				}
				
				sb.append(" p.despesa.categoria.id = :idCategoria ");
				temWhere = Boolean.TRUE;
			}
			
			if(parameterObject.getMes() != null && parameterObject.getMes().intValue() > 0){
				
				if(temWhere){
					sb.append(" and ");
				}else{
					sb.append(" where ");
				}
				
				sb.append(" p.mesDespesa = :mes ");
				temWhere = Boolean.TRUE;
			}
			
				
			if(parameterObject.getAno() != null && parameterObject.getAno().intValue() > 0){
				
				if(temWhere){
					sb.append(" and ");
				}else{
					sb.append(" where ");
				}
				
				sb.append(" p.anoDespesa = :ano ");
				temWhere = Boolean.TRUE;
			}
			
			sb.append(" order by p.despesa.descricao ");
			
			Query query = session.createQuery(sb.toString());
			   
		    if(parameterObject.getDescricao() != null && !"".equals(parameterObject.getDescricao().trim())){
			   query.setParameter("descricao", "%"+parameterObject.getDescricao()+"%");
		    }
		    
		    if(parameterObject.getIdCategoria() != null && parameterObject.getIdCategoria().intValue() > 0){
			   query.setParameter("idCategoria", parameterObject.getIdCategoria());
		    }
		    
		    if(parameterObject.getMes() != null && parameterObject.getMes().intValue() > 0){
			   query.setParameter("mes", parameterObject.getMes());
		    }
		    
		    if(parameterObject.getAno() != null && parameterObject.getAno().intValue() > 0){
			   query.setParameter("ano", parameterObject.getAno());
		    }
		    
		    List<Parcela> despesas = query.getResultList();
		    
		    return despesas;
		    
		}catch(Exception e){
			throw new EJBException(e);
		}
		
	}
	
	/**
	 * Metodo responsavel por verificar se uma categoria possui despesas
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean despesaCategoria(Integer idCategoria){
		
       try{
			
			StringBuilder sb = new StringBuilder("from Despesa d ");
			
			sb.append(" where d.categoria.id  = :idCategoria ");
			
			Query query = session.createQuery(sb.toString());
			   
		    query.setParameter("idCategoria", idCategoria);
		    
		    
		    List<Despesa> despesas = query.getResultList();
		    
		    return despesas.isEmpty();
		    
		}catch(Exception e){
			throw new EJBException(e);
		}
		
	}
	
    /**
     * Metodo responsavel por consultar despesas por periodo
     * @param dataIncial
     * @param dataFinal
     * @return List<DespesaDTO>
     */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultar/{inicio}/{fim}")
	@SuppressWarnings("unchecked")
	public List<Parcela> consultaDespesasPeriodo(@PathParam("inicio")Date dataIncial, @PathParam("fim")Date dataFinal){
		
        try{
        	
        	StringBuilder sb = new StringBuilder("from Parcela p where p.dtVencimento between :dataInicial and :dataFinal  ");
    		
    		Query query = session.createQuery(sb.toString());
    		query.setParameter("dataInicial", dataIncial);
    		query.setParameter("dataFinal", dataFinal);
    	    List<Parcela> despesas = query.getResultList();
    	    
    	    return despesas;
    	    
        }catch(Exception e){
        	throw new EJBException(e);
        }
		
	}
    
    /**
     * Metodo responsavel por consultar os gastos realizados agrupados por ano
     * @param anoInicial
     * @param anoFinal
     * @return List<GastoDTO>
     */
    @SuppressWarnings("unchecked")
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultargastos/{inicio}/{fim}")
	public List<Gasto> consultaGastosIntervaloAnual(@PathParam("inicio")Integer anoInicial, @PathParam("fim")Integer anoFinal){
    	
    	List<Gasto> gastoAnual = new ArrayList<Gasto>();
    	StringBuilder sb = new StringBuilder("select new br.com.maia.entidade.Gasto(p.anoDespesa, sum(p.valor)) from Parcela p  ");
    	                           sb.append("where p.dtVencimento between :dataInicial and :dataFinal ");
    	                           sb.append("group by p.anoDespesa ");
    	                           sb.append("order by p.anoDespesa asc ");
    	                           
    	
    	try{
    		
    		if((anoInicial != null && anoInicial.intValue() <= 0 ) || (anoFinal != null && anoFinal.intValue() <= 0 ) ){
    		  throw new Exception(PoolString.INTERVALO_DATAS_INVALIDO);	
    		}
    		
    		Calendar dataInicial = Calendar.getInstance();
    		Calendar dataFinal = Calendar.getInstance();
    		
    		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
    		dataInicial.set(Calendar.MONTH, 0);
    		dataInicial.set(Calendar.YEAR, anoInicial);
    		dataInicial.set(Calendar.HOUR, 0);
    		dataInicial.set(Calendar.MINUTE, 0);
    		
    		dataFinal.set(Calendar.DAY_OF_MONTH, 31);
    		dataFinal.set(Calendar.MONTH, 11);
    		dataFinal.set(Calendar.YEAR, anoFinal);
    		dataFinal.set(Calendar.HOUR, 23);
    		dataFinal.set(Calendar.MINUTE, 59);
    		
    		
    		Query query = session.createQuery(sb.toString());
    		query.setParameter("dataInicial", dataInicial.getTime());
    		query.setParameter("dataFinal", dataFinal.getTime());
    		
    		gastoAnual = query.getResultList();
    		
    	}catch(Exception e){
    		throw new EJBException(e);
    	}
    	
    	return gastoAnual;
    	
    }
    
    /**
     * Metodo responsavel por consultar os gastos realizados agrupados por mês
     * @param anoInicial
     * @param anoFinal
     * @param mesInicial
     * @param mesFinal
     * @return List<GastoDTO>
     * 
     */
    @SuppressWarnings("unchecked")
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultargastos/{inicio}/{fim}/{mesinicio}/{mesfim}")
	public List<Gasto> consultaGastosIntervaloMensal(@PathParam("inicio")Integer anoInicial, @PathParam("fim")Integer anoFinal, @PathParam("mesinicio")Integer mesInicial, @PathParam("mesfim")Integer mesFinal){
    	
    	List<Gasto> gastoAnual = new ArrayList<Gasto>();
    	
    	StringBuilder sb = new StringBuilder("select new br.com.maia.entidade.Gasto(p.anoDespesa, p.mesDespesa , sum(p.valor)) from Parcela p  ");
    	                           sb.append("where p.dtVencimento between :dataInicial and :dataFinal ");
    	                           sb.append("group by p.anoDespesa, p.mesDespesa ");
    	                           sb.append("order by p.anoDespesa, p.mesDespesa asc ");
    	                           
    	
    	try{
    		
    		if((anoInicial != null && anoInicial.intValue() <= 0 )  || 
    		       (anoFinal != null && anoFinal.intValue() <= 0 )  || 
    		       (mesFinal != null && mesFinal.intValue() <= 0 )  ||
    		       (mesInicial != null && mesInicial.intValue() <= 0 ) ){
    		  throw new Exception(PoolString.INTERVALO_DATAS_INVALIDO);	
    		}
    		
    		Calendar dataInicial = Calendar.getInstance();
    		Calendar dataFinal = Calendar.getInstance();
    		
    		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
    		dataInicial.set(Calendar.MONTH, mesInicial);
    		dataInicial.set(Calendar.YEAR, anoInicial);
    		dataInicial.set(Calendar.HOUR, 0);
    		dataInicial.set(Calendar.MINUTE, 0);
    		
    		dataFinal.set(Calendar.DAY_OF_MONTH, 31);
    		dataFinal.set(Calendar.MONTH, mesFinal);
    		dataFinal.set(Calendar.YEAR, anoFinal);
    		dataFinal.set(Calendar.HOUR, 23);
    		dataFinal.set(Calendar.MINUTE, 59);
    		
    		
    		Query query = session.createQuery(sb.toString());
    		query.setParameter("dataInicial", dataInicial.getTime());
    		query.setParameter("dataFinal", dataFinal.getTime());
    		
    		gastoAnual = query.getResultList();
    		
    	}catch(Exception e){
    		throw new EJBException(e);
    	}
    	
    	
    	return gastoAnual;
    	
    }
    
    /**
     * Metodo responsavel por consultar os gastos realizados agrupados por ano de uma categoria especifica
     * @param idCategoria
     * @param anoInicial
     * @param anoFinal
     * @return List<GastoDTO>
     */
    @SuppressWarnings("unchecked")
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultargastos/{categoria}/{inicio}/{fim}")
	public List<GastoCategoria> consultaGastosCategoriaIntervaloAnual(@PathParam("categoria")Integer idCategoria, @PathParam("inicio")Integer anoInicial, @PathParam("fim")Integer anoFinal){
    	
    	List<GastoCategoria> gastoAnual = new ArrayList<GastoCategoria>();
    	
    	StringBuilder sb = new StringBuilder("select new br.com.maia.entidade.GastoCategoria( c.nomeCategoria , p.anoDespesa, sum(p.valor)) from Despesa d  ");
    	                           sb.append("inner join d.parcelas p ");
    	                           sb.append("inner join d.categoria c ");
    	                           sb.append("where p.dtVencimento between :dataInicial and :dataFinal ");
    	                           
    	                           if(idCategoria != null){
    	                        	   sb.append("and c.id = :idCategoria ");
    	                           }    	                           
    	                           
    	                           sb.append("group by p.anoDespesa, c.nomeCategoria ");
    	                           sb.append("order by p.anoDespesa, c.nomeCategoria asc ");
    	                           
    	
    	try{
    		
    		if((anoInicial != null && anoInicial.intValue() <= 0 ) || (anoFinal != null && anoFinal.intValue() <= 0 )  ){
    		  throw new Exception(PoolString.DADOS_INVALIDOS);	
    		}
    		
    		Calendar dataInicial = Calendar.getInstance();
    		Calendar dataFinal = Calendar.getInstance();
    		
    		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
    		dataInicial.set(Calendar.MONTH, 0);
    		dataInicial.set(Calendar.YEAR, anoInicial);
    		dataInicial.set(Calendar.HOUR, 0);
    		dataInicial.set(Calendar.MINUTE, 0);
    		
    		dataFinal.set(Calendar.DAY_OF_MONTH, 31);
    		dataFinal.set(Calendar.MONTH, 11);
    		dataFinal.set(Calendar.YEAR, anoFinal);
    		dataFinal.set(Calendar.HOUR, 23);
    		dataFinal.set(Calendar.MINUTE, 59);
    		
    		
    		Query query = session.createQuery(sb.toString());
    		query.setParameter("dataInicial", dataInicial.getTime());
    		query.setParameter("dataFinal", dataFinal.getTime());
    		
    		if(idCategoria != null){
    			query.setParameter("idCategoria", idCategoria);
    		}
    		
    		gastoAnual = query.getResultList();
    		
    	}catch(Exception e){
    		throw new EJBException(e);
    	}
    	
    	return gastoAnual;
    	
    }
    
    /**
     * Metodo responsavel por consultar os gastos realizados de uma certa categoria agrupados por mês
     * @param idCategoria
     * @param anoInicial
     * @param anoFinal
     * @param mesInicial
     * @param mesFinal
     * @return List<GastoCategoriaDTO>
     * 
     */
    @SuppressWarnings("unchecked")
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultargastos/{categoria}/{inicio}/{fim}/{mesinicio}/{mesfim}")
	public List<GastoCategoria> consultaGastosCategoriaIntervaloMensal(@PathParam("categoria")Integer idCategoria, @PathParam("inicio")Integer anoInicial, @PathParam("fim")Integer anoFinal, @PathParam("mesinicio")Integer mesInicial, @PathParam("mesfim")Integer mesFinal){
    	
       List<GastoCategoria> gastoAnual = new ArrayList<GastoCategoria>();
    	
       StringBuilder sb = new StringBuilder("select new br.com.maia.entidade.GastoCategoria( c.nomeCategoria , p.anoDespesa, p.mesDespesa , sum(p.valor)) from Despesa d  ");
       								sb.append("inner join d.parcelas p ");
       								sb.append("inner join d.categoria c ");
       								sb.append("where p.dtVencimento between :dataInicial and :dataFinal ");
       
							        if(idCategoria != null){
							    	   sb.append("and c.id = :idCategoria ");
							        }    	                           
       
							        sb.append("group by p.anoDespesa, p.mesDespesa , c.nomeCategoria ");
							        sb.append("order by p.anoDespesa, p.mesDespesa, c.nomeCategoria asc ");

    	                           
    	
    	try{
    		
    		if((anoInicial != null && anoInicial.intValue() <= 0 )  || 
    		       (anoFinal != null && anoFinal.intValue() <= 0 )  || 
    		       (mesFinal != null && mesFinal.intValue() <= 0 )  ||
    		       (mesInicial != null && mesInicial.intValue() <= 0 ) ){
    		  throw new Exception(PoolString.INTERVALO_DATAS_INVALIDO);	
    		}
    		
    		Calendar dataInicial = Calendar.getInstance();
    		Calendar dataFinal = Calendar.getInstance();
    		
    		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
    		dataInicial.set(Calendar.MONTH, mesInicial);
    		dataInicial.set(Calendar.YEAR, anoInicial);
    		dataInicial.set(Calendar.HOUR, 0);
    		dataInicial.set(Calendar.MINUTE, 0);
    		
    		dataFinal.set(Calendar.DAY_OF_MONTH, 31);
    		dataFinal.set(Calendar.MONTH, mesFinal);
    		dataFinal.set(Calendar.YEAR, anoFinal);
    		dataFinal.set(Calendar.HOUR, 23);
    		dataFinal.set(Calendar.MINUTE, 59);
    		
    		
    		Query query = session.createQuery(sb.toString());
    		query.setParameter("dataInicial", dataInicial.getTime());
    		query.setParameter("dataFinal", dataFinal.getTime());
    		
    		if(idCategoria != null){
		    	query.setParameter("idCategoria", idCategoria);
		    }
    		
    		gastoAnual = query.getResultList();
    		
    	}catch(Exception e){
    		throw new EJBException(e);
    	}
    	
    	return gastoAnual;
    	
    }
    
    /**
     * Metodo responsavel por consultar os gastos agrupados por categoria por periodo
     * @param anoInicial
     * @param anoFinal
     * @param mesInicial
     * @param mesFinal
     * @return List<GastoDTO>
     * 
     */
    @SuppressWarnings("unchecked")
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultargastoscategoria/{inicio}/{fim}")
	public List<GastoCategoria> consultaGastosCategoriaPorPeriodo(@PathParam("inicio")Date dataInicial, @PathParam("fim")Date dataFinal){
    	
    	List<GastoCategoria> gastoAnual = new ArrayList<GastoCategoria>();
    	
    	StringBuilder sb = new StringBuilder("select new br.com.maia.entidade.GastoCategoria( c.nomeCategoria ,  sum(p.valor)) from Parcela p  ");
			sb.append("inner join p.despesa d ");
			sb.append("inner join d.categoria c ");
			sb.append("where p.dtVencimento between :dataInicial and :dataFinal ");

        sb.append("group by c.nomeCategoria ");
        sb.append("order by c.nomeCategoria asc ");

       

		try{
		
			if(dataInicial == null || dataFinal == null ) {
				throw new Exception(PoolString.INTERVALO_DATAS_INVALIDO);	
			}
			
			Calendar dataInicialC = Calendar.getInstance();
			Calendar dataFinalC = Calendar.getInstance();
			
			dataInicialC.setTime(dataInicial);
			dataInicialC.set(Calendar.HOUR, 0);
			dataInicialC.set(Calendar.MINUTE, 0);
			
			
			dataFinalC.setTime(dataFinal);
			dataFinalC.set(Calendar.HOUR, 23);
			dataFinalC.set(Calendar.MINUTE, 59);
			
			
			Query query = session.createQuery(sb.toString());
			query.setParameter("dataInicial", dataInicialC.getTime());
			query.setParameter("dataFinal", dataFinalC.getTime());
			
			gastoAnual = query.getResultList();
		
		}catch(Exception e){
			throw new EJBException(e);
		}

    	
    	return gastoAnual;
    	
    }
    
    /**
     * Metodo responsavel por consultar os gastos agrupados por categoria por periodo
     * @param anoInicial
     * @param anoFinal
     * @param mesInicial
     * @param mesFinal
     * @return List<GastoDTO>
     * 
     */
    @SuppressWarnings("unchecked")
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultargastoscategoriames/{inicio}/{fim}")
	public List<GastoCategoria> consultaGastosCategoriaPorPeriodo(@PathParam("inicio")Integer mes, @PathParam("fim")Integer ano){
    	
    	List<GastoCategoria> gastoAnual = new ArrayList<GastoCategoria>();
    	
    	StringBuilder sb = new StringBuilder("select new br.com.maia.entidade.GastoCategoria( c.nomeCategoria ,  sum(p.valor)) from Parcela p  ");
			sb.append("inner join p.despesa d ");
			sb.append("inner join d.categoria c ");
			sb.append("where p.mesDespesa = :mes and p.anoDespesa = :ano ");

        sb.append("group by c.nomeCategoria ");
        sb.append("order by c.nomeCategoria asc ");

       

		try{
		
			if(mes == null) {
				throw new Exception(PoolString.MES_INVALIDO);	
			}
			
			if(ano == null ) {
				throw new Exception(PoolString.ANO_INVALIDO);	
			}
			
			
			Query query = session.createQuery(sb.toString());
			query.setParameter("mes", mes);
			query.setParameter("ano", ano);
			
			gastoAnual = query.getResultList();
		
		}catch(Exception e){
			throw new EJBException(e);
		}

    	
    	return gastoAnual;
    	
    }

    
    @PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/manter")
    public void incluirDespesa (Despesa despesa){
		
		try{
			
			session.persist(despesa);
			
		}catch(Exception e){
			throw new EJBException(e);
		}
		
	}
	
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/manter")
	public void alterarDespesa (Despesa despesa){
		
    	try{
    		Despesa despesaBase = session.find(Despesa.class, despesa.getId());
    		despesaBase.setCategoria(despesa.getCategoria());
    		despesaBase.setDescricao(despesa.getDescricao());
    		despesaBase.setParcelas(despesa.getParcelas());
    		session.merge(despesaBase);
   		}catch(Exception e){
			throw new EJBException(e);
		}
    	
	}
	
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/manterparcela")
    public void alterarParcela (Parcela parcela){
		
    	try{
    		Parcela parcelaBase = session.find(Parcela.class, parcela.getIdParcela());
    		parcelaBase.setDtPagamento(parcela.getDtPagamento());
    		parcelaBase.setValorPago(parcela.getValorPago());
    		session.merge(parcelaBase);
   		}catch(Exception e){
			throw new EJBException(e);
		}
    	
	}
    
    @DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/excluirdespesa/{despesa}")
    public void excluirDespesa (@PathParam("despesa")Integer idDespesa){
		
    	try{
    		Despesa despesa = session.find(Despesa.class, idDespesa);
    		
    		if(despesa == null){
    			throw new EJBException(PoolString.DESPESA_EXC_NAO_LOCALIZADA);
    		}else{
    			session.remove(despesa);
    		}
    		
			
		}catch(Exception e){
			throw new EJBException(e);
		}
    	
	}
    
    @DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/excruirparcela/{parcela}")
    public void excluirParcela (@PathParam("parcela")Integer idParcela){
		
    	try{
    		Parcela parcela = session.find(Parcela.class, idParcela);
    		
    		if(parcela == null){
    			throw new EJBException(PoolString.DESPESA_EXC_NAO_LOCALIZADA);
    		}else{
    			session.remove(parcela);
    			session.flush();
    		}
    		
			
		}catch(Exception e){
			throw new EJBException(e);
		}
    	
	}
    
    @GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consultalimite")
    public Limite consultaLimite(){
    	
    	//TODO COLOCAR TRATAMENTO
    	
    	StringBuilder sb = new StringBuilder(" from Limite ");
    	Query query = session.createQuery(sb.toString());
    	   	
    	Limite limite = (Limite) query.setMaxResults(1).getSingleResult(); 
    	
    	return limite;
    }
    
    
    
    	
}
