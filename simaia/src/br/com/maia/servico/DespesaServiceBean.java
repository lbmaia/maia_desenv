package br.com.maia.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.maia.entidade.Despesa;
import br.com.maia.entidade.Gasto;
import br.com.maia.entidade.GastoCategoria;
import br.com.maia.entidade.Limite;
import br.com.maia.entidade.Parcela;
import br.com.maia.util.PoolString;

@Stateless(mappedName="DespesaServiceBean")
public class DespesaServiceBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1360853253497856474L;
	@PersistenceContext(name="maia-ds")	
	private EntityManager session;
	
	
	/**
	 * Metodo responsavel por consultar despesas por descricao
	 * @param descricao
	 * @return List<DespesaDTO>
	 */
	@SuppressWarnings("unchecked")
	public List<Parcela> consultaDespesas(String descricao, Integer idCategoria, Integer mes, Integer ano){
		
		try{
			
			boolean temWhere = Boolean.FALSE;
			
			StringBuilder sb = new StringBuilder("from Parcela p ");
			
			if(descricao != null && !"".equals(descricao.trim())){
				sb.append(" where p.despesa.descricao like :descricao ");
				temWhere = Boolean.TRUE;
			}
			
			if(idCategoria != null && idCategoria.intValue() > 0){
				
				if(temWhere){
					sb.append(" and ");
				}else{
					sb.append(" where ");
				}
				
				sb.append(" p.despesa.categoria.id = :idCategoria ");
				temWhere = Boolean.TRUE;
			}
			
			if(mes != null && mes.intValue() > 0){
				
				if(temWhere){
					sb.append(" and ");
				}else{
					sb.append(" where ");
				}
				
				sb.append(" p.mesDespesa = :mes ");
				temWhere = Boolean.TRUE;
			}
			
				
			if(ano != null && ano.intValue() > 0){
				
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
			   
		    if(descricao != null && !"".equals(descricao.trim())){
			   query.setParameter("descricao", "%"+descricao+"%");
		    }
		    
		    if(idCategoria != null && idCategoria.intValue() > 0){
			   query.setParameter("idCategoria", idCategoria);
		    }
		    
		    if(mes != null && mes.intValue() > 0){
			   query.setParameter("mes", mes);
		    }
		    
		    if(ano != null && ano.intValue() > 0){
			   query.setParameter("ano", ano);
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
    @SuppressWarnings("unchecked")
	public List<Parcela> consultaDespesasPeriodo(Date dataIncial, Date dataFinal){
		
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
	public List<Gasto> consultaGastosIntervaloAnual(Integer anoInicial, Integer anoFinal){
    	
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
	public List<Gasto> consultaGastosIntervaloMensal(Integer anoInicial, Integer anoFinal, Integer mesInicial, Integer mesFinal){
    	
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
	public List<GastoCategoria> consultaGastosCategoriaIntervaloAnual(Integer idCategoria, Integer anoInicial, Integer anoFinal){
    	
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
	public List<GastoCategoria> consultaGastosCategoriaIntervaloMensal(Integer idCategoria, Integer anoInicial, Integer anoFinal, Integer mesInicial, Integer mesFinal){
    	
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
	public List<GastoCategoria> consultaGastosCategoriaPorPeriodo(Date dataInicial, Date dataFinal){
    	
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
	public List<GastoCategoria> consultaGastosCategoriaPorPeriodo(Integer mes, Integer ano){
    	
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

    
    
    public void incluirDespesa (Despesa despesa){
		
		try{
			
			session.persist(despesa);
			
		}catch(Exception e){
			throw new EJBException(e);
		}
		
	}
	
    
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
    
   
    public void excluirDespesa (Integer idDespesa){
		
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
    
    public void excluirParcela (Integer idParcela){
		
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
    
    public Limite consultaLimite(){
    	
    	//TODO COLOCAR TRATAMENTO
    	
    	StringBuilder sb = new StringBuilder(" from Limite ");
    	Query query = session.createQuery(sb.toString());
    	   	
    	Limite limite = (Limite) query.setMaxResults(1).getSingleResult(); 
    	
    	return limite;
    }
    
    
    
    	
}
