package br.com.maia.bean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import br.com.maia.entidade.Categoria;
import br.com.maia.entidade.Gasto;
import br.com.maia.entidade.GastoCategoria;
import br.com.maia.entidade.Parcela;
import br.com.maia.enums.EMes;
import br.com.maia.enums.ETipoRelatorio;
import br.com.maia.servico.CategoriaServiceBean;
import br.com.maia.servico.DespesaServiceBean;
import br.com.maia.util.PoolString;
import br.com.maia.util.Util;

@ManagedBean(name="RelatoriosBean")
@ViewScoped
public class RelatoriosBean implements Serializable{
	
	@EJB
	DespesaServiceBean despesaService;
	
	@EJB
	CategoriaServiceBean categoriaService;

	/**
	 * 
	 */
	private static final long serialVersionUID = 847007265679817884L;
	
	private List<Gasto> gastos;
	private List<GastoCategoria> gastosCategoria;
	private List<Parcela> parcelas;
	private int tipoRelatorio;
	private Date dataInicial;
	private Date dataFinal;
	private Integer idCategoria;
	private boolean exibeDataPerido;
	private boolean exibeDataPeridoCategoria;
	private boolean exibeDataAnualGeral;
	private boolean exibeDataAnualCategoria;
	private boolean exibeDataMesGeral;
	private boolean exibeDataMesCategoria;
	private boolean exibeResultado;
	private boolean exibeBotao;
	private boolean exibeInput;
	private boolean exibeCombocategoria;
	private List<Categoria> categorias;
	private CartesianChartModel categoryModel;
	private PieChartModel pieModel;
	private Integer max;
	private static final String RELATORIO_DESPESA_PERIODO = "despesaPeriodo.jasper";
	private static final String RELATORIO_DESPESA_PERIODO_IMG = "wood.jpg";

	
	
	public void selecionaRelatorio(){
		
		limpaForm();
		
		switch (ETipoRelatorio.retornaTipoRelatorio(tipoRelatorio)) {
		case GASTO_ANUAL:
			
			exibeDataPerido = Boolean.FALSE;
			exibeDataAnualGeral = Boolean.TRUE;
			exibeDataAnualCategoria = Boolean.FALSE;
			exibeDataMesGeral = Boolean.FALSE;
			exibeDataMesCategoria = Boolean.FALSE;
			exibeBotao = Boolean.TRUE;
			exibeInput = Boolean.TRUE;
			break;
        case GASTO_ANUAL_CATEGORIA:
        	
        	exibeDataPerido = Boolean.FALSE;
			exibeDataAnualGeral = Boolean.FALSE;
			exibeDataAnualCategoria = Boolean.TRUE;
			exibeDataMesGeral = Boolean.FALSE;
			exibeDataMesCategoria = Boolean.FALSE;
			exibeBotao = Boolean.TRUE;
			exibeInput = Boolean.TRUE;
			exibeCombocategoria = Boolean.TRUE;
			break;	
        case GASTO_MENSAL:
        	
        	exibeDataPerido = Boolean.FALSE;
			exibeDataAnualGeral = Boolean.FALSE;
			exibeDataAnualCategoria = Boolean.FALSE;
			exibeDataMesGeral = Boolean.TRUE;
			exibeDataMesCategoria = Boolean.FALSE;
			exibeBotao = Boolean.TRUE;
			exibeInput = Boolean.TRUE;
        	break;
        case GASTO_MENSAL_CATEGORIA:
        	
        	exibeDataPerido = Boolean.FALSE;
			exibeDataAnualGeral = Boolean.FALSE;
			exibeDataAnualCategoria = Boolean.FALSE;
			exibeDataMesGeral = Boolean.FALSE;
			exibeDataMesCategoria = Boolean.TRUE;
			exibeBotao = Boolean.TRUE;
			exibeInput = Boolean.TRUE;
			exibeCombocategoria = Boolean.TRUE;
        	break;
        case GASTO_CATEGORIA_PERIODO:
        	
        	exibeDataPerido = Boolean.TRUE;
			exibeDataAnualGeral = Boolean.FALSE;
			exibeDataAnualCategoria = Boolean.FALSE;
			exibeDataMesGeral = Boolean.FALSE;
			exibeDataMesCategoria = Boolean.FALSE;
			exibeBotao = Boolean.TRUE;
			exibeInput = Boolean.TRUE;
			exibeCombocategoria = Boolean.FALSE;
        	break;
        case GASTO_PERIODO:            
        	
        	exibeDataPerido = Boolean.TRUE;
			exibeDataAnualGeral = Boolean.FALSE;
			exibeDataAnualCategoria = Boolean.FALSE;
			exibeDataMesGeral = Boolean.FALSE;
			exibeDataMesCategoria = Boolean.FALSE;
			exibeBotao = Boolean.TRUE;
			exibeInput = Boolean.TRUE;
        	break;
		default:
			exibeBotao = Boolean.FALSE;
			exibeInput = Boolean.FALSE;
			exibeCombocategoria = Boolean.FALSE;
			break;
		}
		
		
	}
	
	@PostConstruct
	private void init(){
		limpaForm();
	}
	
	public String consultaRelatorioDespesa(){
		
		try{
			
			if(tipoRelatorio == 0){
				Util.addWarn(PoolString.TIPO_REL_INVALIDO);
				return null;
			}
			
			if(dataInicial == null || dataFinal == null){
				Util.addWarn(PoolString.INTERVALO_DATAS_INVALIDO);
				return null;
			}
			
			if(dataInicial.after(dataFinal)){
				Util.addWarn(PoolString.INTERVALO_DATAS_INVALIDO);
				return null;
			}
			
			if(idCategoria != null && idCategoria.intValue() == 0 ){
				idCategoria = null;
			}
						
			Calendar dataInicialC = Calendar.getInstance();
			Calendar dataFinalC = Calendar.getInstance();
			
			dataInicialC.setTime(dataInicial);
			dataFinalC.setTime(dataFinal);
			
			switch (ETipoRelatorio.retornaTipoRelatorio(tipoRelatorio)) {
			case GASTO_ANUAL:
				
				gastos = despesaService.consultaGastosIntervaloAnual(dataInicialC.get(Calendar.YEAR), dataFinalC.get(Calendar.YEAR));
				
				if(gastos.isEmpty()){
					Util.addError(PoolString.SEM_RESULTADO);
				}else{
					categoryModel = new CartesianChartModel();  
					ChartSeries chartDespesas = new ChartSeries();  
			        chartDespesas.setLabel("Despesas");
			        
			        Integer anoInicial = gastos.get(0).getAno()-1;
			        Integer anoFinal = gastos.get(gastos.size()-1).getAno()+1;
			        max = 0;
			        chartDespesas.set(String.valueOf(anoInicial), 0);
					for(Gasto gasto : gastos){
						
						if(max.intValue() < gasto.getValorTotal().intValue()){
							max = gasto.getValorTotal().intValue();
						}
						
						chartDespesas.set(String.valueOf(gasto.getAno()), gasto.getValorTotal());  
				        
					}
					
					max += max;
					
					chartDespesas.set(String.valueOf(anoFinal), 0);
					categoryModel.addSeries(chartDespesas);
				}
				
				break;
            case GASTO_ANUAL_CATEGORIA:
            	
            	gastosCategoria = despesaService.consultaGastosCategoriaIntervaloAnual(getIdCategoria(), dataInicialC.get(Calendar.YEAR), dataFinalC.get(Calendar.YEAR));
            	
            	if(gastosCategoria.isEmpty()){
					Util.addError(PoolString.SEM_RESULTADO);
				}else{
					categoryModel = new CartesianChartModel();  
					ChartSeries chartDespesas = new ChartSeries();  
			        
					Map<String,ChartSeries> series = new HashMap<String,ChartSeries>();
					Map<String,ChartSeries> seriesIncluidas = new HashMap<String,ChartSeries>();
					
					
			        String categoria = gastosCategoria.get(0).getNomeCategoria();
			        chartDespesas.setLabel(categoria);
			        Integer anoInicial = gastosCategoria.get(0).getAno() -1;
			        Integer anoFinal = gastosCategoria.get(gastosCategoria.size()-1).getAno()+1;
			        
			        series.put(categoria, chartDespesas);
			        
			        max = 0;
			        chartDespesas.set(String.valueOf(anoInicial), 0);
					for(GastoCategoria gasto : gastosCategoria){
						
						if(!categoria.equalsIgnoreCase(gasto.getNomeCategoria())){
							
							if(seriesIncluidas.get(categoria) == null){
								categoryModel.addSeries(chartDespesas);
								seriesIncluidas.put(categoria, chartDespesas);
							}						
							
							if(series.get(gasto.getNomeCategoria()) == null){
								chartDespesas = new ChartSeries();
								chartDespesas.setLabel(gasto.getNomeCategoria());
								chartDespesas.set(String.valueOf(anoInicial), 0);
								categoria = gasto.getNomeCategoria(); 
								series.put(categoria, chartDespesas);
							}else{
								chartDespesas = series.get(gasto.getNomeCategoria());
								categoria = gasto.getNomeCategoria();
							}
							
							
							
						}
						
						if(max.intValue() < gasto.getValorTotal().intValue()){
							max = gasto.getValorTotal().intValue();
						}
						
						chartDespesas.set(String.valueOf(gasto.getAno()), gasto.getValorTotal());  
				        
					}
					
					max += max;
					
					if(seriesIncluidas.get(categoria) == null){
						chartDespesas.set(String.valueOf(anoFinal), 0);
						categoryModel.addSeries(chartDespesas);
					}
					
					for(ChartSeries chart : categoryModel.getSeries()){
						chart.set(String.valueOf(anoFinal), 0);
					}
				}
				
            	
            	
            	
				break;	
            case GASTO_MENSAL:
            	
            	gastos = despesaService.consultaGastosIntervaloMensal(dataInicialC.get(Calendar.YEAR), dataFinalC.get(Calendar.YEAR), dataInicialC.get(Calendar.MONTH)+1, dataFinalC.get(Calendar.MONTH)+1);
            	
            	if(gastos.isEmpty()){
					Util.addError(PoolString.SEM_RESULTADO);
				}else{
					categoryModel = new CartesianChartModel();  
					ChartSeries chartDespesas = new ChartSeries();  
			        chartDespesas.setLabel("Despesas");
			        
			        Integer mesInicial = gastos.get(0).getMes().getMes()-1;
			        Integer mesFinal = gastos.get(gastos.size()-1).getMes().getMes()+1;
			        Integer anoInicial = gastos.get(0).getAno()-1;
			        Integer anoFinal = gastos.get(gastos.size()-1).getAno();
			        max = 0;
			        chartDespesas.set(EMes.recuperaMes(mesInicial).getMes()+"/"+anoInicial, 0);
					for(Gasto gasto : gastos){
						
						if(max.intValue() < gasto.getValorTotal().intValue()){
							max = gasto.getValorTotal().intValue();
						}
						
						chartDespesas.set(gasto.getMes().getMes()+"/"+gasto.getAno(), gasto.getValorTotal());  
				        
					}
					
					max += max;
					
					chartDespesas.set(EMes.recuperaMes(mesFinal).getMes()+"/"+anoFinal, 0);
					categoryModel.addSeries(chartDespesas);
				}
            	
            	
            	
            	break;
            case GASTO_MENSAL_CATEGORIA:
            	
            	gastosCategoria = despesaService.consultaGastosCategoriaIntervaloMensal(idCategoria, dataInicialC.get(Calendar.YEAR), dataFinalC.get(Calendar.YEAR), dataInicialC.get(Calendar.MONTH)+1, dataFinalC.get(Calendar.MONTH)+1);
            	
            	if(gastosCategoria.isEmpty()){
					Util.addError(PoolString.SEM_RESULTADO);
				}else{
					categoryModel = new CartesianChartModel();  
					ChartSeries chartDespesas = new ChartSeries();  
			        
					Map<String,ChartSeries> series = new HashMap<String,ChartSeries>();
					Map<String,ChartSeries> seriesIncluidas = new HashMap<String,ChartSeries>();
					
					
			        String categoria = gastosCategoria.get(0).getNomeCategoria();
			        chartDespesas.setLabel(categoria);
			        Integer mesInicial = gastosCategoria.get(0).getMes().getMes() -1;
			        Integer mesFinal = gastosCategoria.get(gastosCategoria.size()-1).getMes().getMes()+1;
			        Integer anoInicial = gastosCategoria.get(0).getAno() -1;
			        Integer anoFinal = gastosCategoria.get(gastosCategoria.size()-1).getAno();
			        
			        series.put(categoria, chartDespesas);
			        
			        max = 0;
			        chartDespesas.set(EMes.recuperaMes(mesInicial).getMes()+"/"+anoInicial, 0);
					for(GastoCategoria gasto : gastosCategoria){
						
						if(!categoria.equalsIgnoreCase(gasto.getNomeCategoria())){
							
							if(seriesIncluidas.get(categoria) == null){
								categoryModel.addSeries(chartDespesas);
								seriesIncluidas.put(categoria, chartDespesas);
							}						
							
							if(series.get(gasto.getNomeCategoria()) == null){
								chartDespesas = new ChartSeries();
								chartDespesas.setLabel(gasto.getNomeCategoria());
								chartDespesas.set(EMes.recuperaMes(mesInicial).getMes()+"/"+anoInicial, 0);
								categoria = gasto.getNomeCategoria(); 
								series.put(categoria, chartDespesas);
							}else{
								chartDespesas = series.get(gasto.getNomeCategoria());
								categoria = gasto.getNomeCategoria();
							}
							
							
							
						}
						
						if(max.intValue() < gasto.getValorTotal().intValue()){
							max = gasto.getValorTotal().intValue();
						}
						
						chartDespesas.set(gasto.getMes().getMes()+"/"+gasto.getAno(), gasto.getValorTotal());  
				        
					}
					
					max += max;
					
					if(seriesIncluidas.get(categoria) == null){
						chartDespesas.set(EMes.recuperaMes(mesFinal).getMes()+"/"+anoFinal, 0);
						categoryModel.addSeries(chartDespesas);
					}
					
					for(ChartSeries chart : categoryModel.getSeries()){
						chart.set(EMes.recuperaMes(mesFinal).getMes()+"/"+anoFinal, 0);
					}
				}
            	
            	
				
            	break;
            case GASTO_CATEGORIA_PERIODO:
            	
            	gastosCategoria = despesaService.consultaGastosCategoriaPorPeriodo(dataInicial, dataFinal);
            	
            	if(gastosCategoria.isEmpty()){
					Util.addError(PoolString.SEM_RESULTADO);
				}else{
					exibeDataPeridoCategoria = Boolean.TRUE;
	            	
	            	pieModel = new PieChartModel();  
	            	
	            	for(GastoCategoria gasto : gastosCategoria){
	            		pieModel.set(gasto.getNomeCategoria(), gasto.getValorTotal());
	            	}
				}
            	                            	
            	break;
            case GASTO_PERIODO:            
            	
            	parcelas = despesaService.consultaDespesasPeriodo(dataInicial, dataFinal);
            	
            	if(parcelas.isEmpty()){
					Util.addError(PoolString.SEM_RESULTADO);   
				}else{
					JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(parcelas); 
					String nomeArq =  Util.class.getResource(RELATORIO_DESPESA_PERIODO_IMG).getPath();
					InputStream relatorio = Util.class.getResourceAsStream(RELATORIO_DESPESA_PERIODO); 
					Map<String, Object> parametros = new HashMap<String, Object>();
					parametros.put("caminhoImg", nomeArq);
					
					HttpServletResponse httpServletResponse=(HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
					httpServletResponse.setContentType("application/pdf");
				    httpServletResponse.addHeader("Content-disposition", "attachment; filename=despesaPeriodo.pdf");  
				    ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();  
				    JasperExportManager.exportReportToPdfStream(JasperFillManager.fillReport(relatorio, parametros, jrds), servletOutputStream);  
				    FacesContext.getCurrentInstance().responseComplete();     
				}
            	
            	break;
			default:
				break;
			}
			
			exibeResultado = Boolean.TRUE;
			
		}catch(Exception e){
			Util.addError(e.getMessage());
		}
		
		return null;
	}
	
	public List<Gasto> getGastos() {
		return gastos;
	}
	
	public void setGastos(List<Gasto> gastos) {
		this.gastos = gastos;
	}

	public List<GastoCategoria> getGastosCategoria() {
		return gastosCategoria;
	}

	public void setGastosCategoria(List<GastoCategoria> gastosCategoria) {
		this.gastosCategoria = gastosCategoria;
	}

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public List<Parcela> getParcelas() {
		return parcelas;
	}

	public void setParcelas(List<Parcela> parcelas) {
		this.parcelas = parcelas;
	}

	public boolean isExibeDataPerido() {
		return exibeDataPerido;
	}

	public void setExibeDataPerido(boolean exibeDataPerido) {
		this.exibeDataPerido = exibeDataPerido;
	}

	public boolean isExibeDataAnualGeral() {
		return exibeDataAnualGeral;
	}

	public void setExibeDataAnualGeral(boolean exibeDataAnualGeral) {
		this.exibeDataAnualGeral = exibeDataAnualGeral;
	}

	public boolean isExibeDataAnualCategoria() {
		return exibeDataAnualCategoria;
	}

	public void setExibeDataAnualCategoria(boolean exibeDataAnualCategoria) {
		this.exibeDataAnualCategoria = exibeDataAnualCategoria;
	}

	public boolean isExibeDataMesGeral() {
		return exibeDataMesGeral;
	}

	public void setExibeDataMesGeral(boolean exibeDataMesGeral) {
		this.exibeDataMesGeral = exibeDataMesGeral;
	}

	public boolean isExibeDataMesCategoria() {
		return exibeDataMesCategoria;
	}

	public void setExibeDataMesCategoria(boolean exibeDataMesCategoria) {
		this.exibeDataMesCategoria = exibeDataMesCategoria;
	}
	
	private void limpaForm(){
		exibeDataPerido = Boolean.FALSE;
		exibeDataAnualGeral = Boolean.FALSE;
		exibeDataPeridoCategoria = Boolean.FALSE;
		exibeDataAnualCategoria = Boolean.FALSE;
		exibeDataMesGeral = Boolean.FALSE;
		exibeDataMesCategoria = Boolean.FALSE;
		exibeResultado = Boolean.FALSE;
		exibeBotao = Boolean.FALSE;
		exibeInput = Boolean.FALSE;
		exibeCombocategoria = Boolean.FALSE;
		categoryModel = null;
		dataInicial = null;
		dataFinal = null;
		idCategoria = null;
		gastos = new ArrayList<Gasto>();
		gastosCategoria = new ArrayList<GastoCategoria>();
		parcelas = new ArrayList<Parcela>();
	}
	
    public List<Categoria> getCategorias() {
		
		if(this.categorias == null || (this.categorias != null && this.categorias.isEmpty())){
			try {
				categorias = categoriaService.consultaCategorias(null);
				
			} catch (Exception e) {
				categorias = new ArrayList<Categoria>();
			} 
		}
		
		return categorias;
	}
    
    public boolean isListaGastosVazia(){
		return this.getGastos().isEmpty() ;
	}
    
    public boolean isListaGastosCategoriaVazia(){
		return this.getGastosCategoria().isEmpty() ;
	}
    
    public ETipoRelatorio[] getTipoRelatorios(){
    	return ETipoRelatorio.values();
    }

	public boolean isExibeResultado() {
		return exibeResultado;
	}

	public void setExibeResultado(boolean exibeResultado) {
		this.exibeResultado = exibeResultado;
	}

	public boolean isExibeBotao() {
		return exibeBotao;
	}

	public void setExibeBotao(boolean exibeBotao) {
		this.exibeBotao = exibeBotao;
	}

	public boolean isExibeInput() {
		return exibeInput;
	}

	public void setExibeInput(boolean exibeInput) {
		this.exibeInput = exibeInput;
	}

	public boolean isExibeCombocategoria() {
		return exibeCombocategoria;
	}

	public void setExibeCombocategoria(boolean exibeCombocategoria) {
		this.exibeCombocategoria = exibeCombocategoria;
	}

	public CartesianChartModel getCategoryModel() {
		return categoryModel;
	}

	public void setCategoryModel(CartesianChartModel categoryModel) {
		this.categoryModel = categoryModel;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public PieChartModel getPieModel() {
		return pieModel;
	}

	public void setPieModel(PieChartModel pieModel) {
		this.pieModel = pieModel;
	}

	public boolean isExibeDataPeridoCategoria() {
		return exibeDataPeridoCategoria;
	}

	public void setExibeDataPeridoCategoria(boolean exibeDataPeridoCategoria) {
		this.exibeDataPeridoCategoria = exibeDataPeridoCategoria;
	}

}
