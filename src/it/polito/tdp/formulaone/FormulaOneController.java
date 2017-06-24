package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	private Model model;
	private Season ultimaSeasonPerCuiHoCreatoIlGrafo;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Season> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	if(boxAnno.getValue()==null){
    		txtResult.appendText("Sciegliere un anno \n");
    		return;
    	}
    	//model.CreaGrafoOttimizzato(boxAnno.getValue());
    	//model.CreaGrafoSemiOttimizzato(boxAnno.getValue());
    	model.CreaGrafoNONOttimizzato(boxAnno.getValue());
    	
    	txtResult.appendText(model.MigliorPilota().toString()+"\n");
    	
    	ultimaSeasonPerCuiHoCreatoIlGrafo=boxAnno.getValue();
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    		if(boxAnno.getValue()!=null && boxAnno.getValue()==ultimaSeasonPerCuiHoCreatoIlGrafo ){
		    	int k ;
		    	try {
		    		k = Integer.parseInt(textInputK.getText());
		    	} catch (NumberFormatException e) {
		    		txtResult.appendText("ERRORE: k deve essere in formato numerico \n");
		    		return;
		    	}
		    	
		    	if(k<1) {
		    		txtResult.appendText("ERRORE: k deve essere > 0 \n");
		    		return;
		    	}
		    	
		    	txtResult.appendText(model.InterfacciaRicorsione(k).toString()+"\n");
		    	
    		}else{
    			txtResult.appendText("Crea prima il grafo per l'anno che vuoi esaminare ! \n");
    			return;
    		}
    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	this.model = model;
    	boxAnno.getItems().addAll(model.getStagioni());
    }
}
