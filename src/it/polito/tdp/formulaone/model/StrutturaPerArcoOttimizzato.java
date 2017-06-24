package it.polito.tdp.formulaone.model;

public class StrutturaPerArcoOttimizzato {
	private Driver vincente;
	private Driver perdente;
	private int numeroVittorie;
	public StrutturaPerArcoOttimizzato(Driver vincente, Driver perdente, int numeroVittorie) {
		super();
		this.vincente = vincente;
		this.perdente = perdente;
		this.numeroVittorie = numeroVittorie;
	}
	public Driver getVincente() {
		return vincente;
	}
	public void setVincente(Driver vincente) {
		this.vincente = vincente;
	}
	public Driver getPerdente() {
		return perdente;
	}
	public void setPerdente(Driver perdente) {
		this.perdente = perdente;
	}
	public int getNumeroVittorie() {
		return numeroVittorie;
	}
	public void setNumeroVittorie(int numeroVittorie) {
		this.numeroVittorie = numeroVittorie;
	}
	
	

}
