package br.com.maia.datamodel;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.maia.entidade.Parcela;

public class ParcelaDataModel extends ListDataModel<Parcela> implements SelectableDataModel<Parcela> {

	@SuppressWarnings("unchecked")
	@Override
	public Parcela getRowData(String rowKey) {
		List<Parcela> parcelas = (List<Parcela>) getWrappedData();  
        
        for(Parcela parcela : parcelas) {  
            if(parcela.getIdParcela().toString().equals(rowKey))  
                return parcela;  
        }  
          
        return null; 
	}

	@Override
	public Object getRowKey(Parcela parcela) {
		return parcela.getIdParcela();
	}
	
	public ParcelaDataModel() {  
    }  
  
    public ParcelaDataModel(List<Parcela> data) {  
        super(data);  
    }  
      
   
}
