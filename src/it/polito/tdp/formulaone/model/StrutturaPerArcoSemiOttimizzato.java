package it.polito.tdp.formulaone.model;

public class StrutturaPerArcoSemiOttimizzato {
	private Driver perdente;
	private int numeroVittorie;
	public StrutturaPerArcoSemiOttimizzato(Driver perdente, int numeroVittorie) {
		super();
		this.perdente = perdente;
		this.numeroVittorie = numeroVittorie;
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
