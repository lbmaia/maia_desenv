package br.com.maia.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.MeterGaugeChartModel;

import br.com.maia.entidade.GastoCategoria;
import br.com.maia.entidade.Limite;
import br.com.maia.servico.DespesaServiceBean;
import br.com.maia.util.PoolString;
import br.com.maia.util.Util;

@ManagedBean
@ViewScoped
public class LimitesBean implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3808698759875538784L;

	@EJB
	private DespesaServiceBean despesaService;
	
	private Limite limite;
	private Integer mes;
	private Integer ano;
	private MeterGaugeChartModel medidorGastosPlanejados;
	private MeterGaugeChartModel medidorGastosRealizados;
	private Number valorGastoTotalPlanejado;
	private Number valorGastoTotalRealizado;
	private List<Number> intervalos;
	private List<GastoCategoria> gastos;
	private BigDecimal valorTotalDespesaPlanejado;
	private BigDecimal valorTotalDespesaRealizado;
	
	@PostConstruct
	public void init(){
		limite = despesaService.consultaLimite();
		valorGastoTotalPlanejado = BigDecimal.ZERO;
		setValorTotalDespesaPlanejado(BigDecimal.ZERO);
		valorGastoTotalRealizado = BigDecimal.ZERO;
		setValorTotalDespesaRealizado(BigDecimal.ZERO);
		montaIntervalos();
		setMedidorGastosPlanejados(new MeterGaugeChartModel(valorGastoTotalPlanejado, intervalos, intervalos));
		setMedidorGastosRealizados(new MeterGaugeChartModel(valorGastoTotalRealizado, intervalos, intervalos));
	}

	private void montaIntervalos() {
		intervalos = new ArrayList<Number>();
		intervalos.add(BigDecimal.ZERO);
		intervalos.add(new BigDecimal("4000"));
		intervalos.add(new BigDecimal("8000"));
		intervalos.add(limite.getLimiteAlerta());
		intervalos.add(limite.getLimiteMaximo());
		
	}
	
	public String consultaDespesas(){
		
		if( !Util.isBlankorNull(mes) && !(mes > 0  && mes <=12)){
    		Util.addInfo(PoolString.MES_INVALIDO);
    		return null;
    	}
    	
    	if( !Util.isBlankorNull(ano) && (ano < 2000)){
    		Util.addInfo(PoolString.ANO_INVALIDO);
    		return null;
    	}
    	
    	gastos = despesaService.consultaGastosCategoriaPorPeriodo(mes, ano);
    	BigDecimal valorTotalPlanejado = BigDecimal.ZERO;
    	BigDecimal valorTotalRealizado = BigDecimal.ZERO;
    	
    	for(GastoCategoria gasto : gastos){
    		valorTotalPlanejado = valorTotalPlanejado.add(gasto.getValorTotal());
    		valorTotalRealizado = valorTotalRealizado.add(gasto.getValorTotalRealizado());
    	}
    	
    	valorGastoTotalPlanejado = valorTotalPlanejado;
    	valorGastoTotalRealizado = valorTotalRealizado;
    	setValorTotalDespesaPlanejado(valorTotalPlanejado);
    	setValorTotalDespesaRealizado(valorTotalRealizado);
    	setMedidorGastosPlanejados(new MeterGaugeChartModel(valorGastoTotalPlanejado, intervalos));
    	setMedidorGastosRealizados(new MeterGaugeChartModel(valorGastoTotalRealizado, intervalos));
    	
		return null;
	}

	public Limite getLimite() {
		return limite;
	}

	public void setLimite(Limite limite) {
		this.limite = limite;
	}
	
	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public List<GastoCategoria> getGastos() {
		return gastos;
	}

	public void setGastos(List<GastoCategoria> gastos) {
		this.gastos = gastos;
	}

	public MeterGaugeChartModel getMedidorGastosPlanejados() {
		return medidorGastosPlanejados;
	}

	public void setMedidorGastosPlanejados(MeterGaugeChartModel medidorGastosPlanejados) {
		this.medidorGastosPlanejados = medidorGastosPlanejados;
	}

	public MeterGaugeChartModel getMedidorGastosRealizados() {
		return medidorGastosRealizados;
	}

	public void setMedidorGastosRealizados(MeterGaugeChartModel medidorGastosRealizados) {
		this.medidorGastosRealizados = medidorGastosRealizados;
	}

	public BigDecimal getValorTotalDespesaPlanejado() {
		return valorTotalDespesaPlanejado;
	}

	public void setValorTotalDespesaPlanejado(BigDecimal valorTotalDespesaPlanejado) {
		this.valorTotalDespesaPlanejado = valorTotalDespesaPlanejado;
	}

	public BigDecimal getValorTotalDespesaRealizado() {
		return valorTotalDespesaRealizado;
	}

	public void setValorTotalDespesaRealizado(BigDecimal valorTotalDespesaRealizado) {
		this.valorTotalDespesaRealizado = valorTotalDespesaRealizado;
	}
	
	
}
