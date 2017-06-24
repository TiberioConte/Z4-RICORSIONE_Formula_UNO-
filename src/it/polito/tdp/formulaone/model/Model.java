package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashSet;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	private FormulaOneDAO dao;
	private ArrayList<Season> stagioni;
	private SimpleDirectedWeightedGraph<Driver,DefaultWeightedEdge> grafo;
	private DriverIdMap mappaPiloti ;
	
	//ricorsione 
	HashSet<Driver>  soluzioneOttima;
	int idMaggiore;
	
	

	public Model() {
		dao= new FormulaOneDAO();
		stagioni= dao.getAllSeasons();
		this.mappaPiloti = new DriverIdMap();
	}

	public ArrayList<Season> getStagioni() {
		return stagioni;
	}

	public void CreaGrafoOttimizzato(Season s){
		grafo =new SimpleDirectedWeightedGraph<Driver,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//aggiungo i vertici
		Graphs.addAllVertices(grafo, dao.getTuttiIPIlotiHannoFinitoGaraNella(s, mappaPiloti));
		System.out.println("Ho aggiunto vertici :"+grafo.vertexSet().size());
		//aggiungo gli archi 
		for(StrutturaPerArcoOttimizzato arco:dao.getArchiOttimizzati(s,mappaPiloti)){
			DefaultWeightedEdge a =grafo.addEdge(arco.getVincente(), arco.getPerdente());
			grafo.setEdgeWeight(a, arco.getNumeroVittorie());
		}
		System.out.println("Ho aggiunto archi :"+grafo.edgeSet().size());
	//	for(DefaultWeightedEdge arco:grafo.edgeSet()){
	//		System.out.println(arco+" "+grafo.getEdgeWeight(arco));
			
	//	}
	}
	public void CreaGrafoSemiOttimizzato(Season s){
		grafo =new SimpleDirectedWeightedGraph<Driver,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//aggiungo i vertici
		Graphs.addAllVertices(grafo, dao.getTuttiIPIlotiHannoFinitoGaraNella(s, mappaPiloti));
		System.out.println("Ho aggiunto vertici :"+grafo.vertexSet().size());
		//aggiungo gli archi 
		for(Driver pilota:grafo.vertexSet()){
				for(StrutturaPerArcoSemiOttimizzato arco:dao.getArchiSemiOttimizzati(s, mappaPiloti,pilota)){
					DefaultWeightedEdge a =grafo.addEdge(pilota, arco.getPerdente());
					grafo.setEdgeWeight(a, arco.getNumeroVittorie());
				}
		}
		System.out.println("Ho aggiunto archi :"+grafo.edgeSet().size());
	}
	public void CreaGrafoNONOttimizzato(Season s){
		grafo =new SimpleDirectedWeightedGraph<Driver,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//aggiungo i vertici
		Graphs.addAllVertices(grafo, dao.getTuttiIPIlotiHannoFinitoGaraNella(s, mappaPiloti));
		System.out.println("Ho aggiunto vertici :"+grafo.vertexSet().size());
		//aggiungo gli archi 
		for(Driver vincente:grafo.vertexSet()){
			for(Driver perdente:grafo.vertexSet()){
				if(!vincente.equals(perdente)){
					int numeroVittorie=dao.getArchiNonOttimizzati(s, mappaPiloti,vincente,perdente);
					if(numeroVittorie>0){
						DefaultWeightedEdge a =grafo.addEdge(vincente, perdente);
						grafo.setEdgeWeight(a, numeroVittorie);
					}
				}
			}
		}
		System.out.println("Ho aggiunto archi :"+grafo.edgeSet().size());
	}
	public Driver MigliorPilota(){
		int punteggioMassimo=Integer.MIN_VALUE;
		Driver migliore=null ;
		
		for(Driver d:grafo.vertexSet()){
			int punteggio=0;
			for(Driver vincitore:Graphs.predecessorListOf(grafo, d)){
				punteggio=(int) (punteggio-grafo.getEdgeWeight(grafo.getEdge(vincitore,d)));
			}
			for(Driver perdente:Graphs.successorListOf(grafo, d)){
				punteggio=(int) (punteggio+grafo.getEdgeWeight(grafo.getEdge(d,perdente)));
			}
			if(punteggio>punteggioMassimo){
				punteggioMassimo=punteggio;
				migliore=d;
			}
			
		}
		return migliore;
	}
	public HashSet<Driver> InterfacciaRicorsione (int k){
		HashSet<Driver>  soluzioneParziale= new HashSet<Driver>();
		soluzioneOttima=new HashSet<Driver>();
		int livello=0;
		this.cercaIdMaggiore();
		this.Ricorsivo(soluzioneParziale,livello,k,null);
		return soluzioneOttima;
	}

	private void Ricorsivo(HashSet<Driver> soluzioneParziale, int livello, int k,Driver ultimoAggiunto) {
		//blocco di salvataggio
		if(livello==k){
			if(PunteggioTeam(soluzioneParziale)<PunteggioTeam(soluzioneOttima)){
				soluzioneOttima.clear();
				soluzioneOttima.addAll(soluzioneParziale);
				System.out.println(soluzioneOttima.toString());
			}
			return;
		}
		if(soluzioneParziale.size()>0&&ultimoAggiunto.getDriverId()==idMaggiore)
			return;
		//aggiungo questa seconda condizione di terminazione per evitare quando in ultima posizione ho il
		//pilota con id maggiore di cercare di aggiungere ugualmente piloti , in quanto so che a causa del ciclo
		//if nel for non ci riusciro, l'albero ricorsivo con questo return rislae velocemente i livelli e si termina.
		
		for(Driver d:grafo.vertexSet()){
			if(soluzioneParziale.size()==0||ultimoAggiunto.compareTo(d)<0){
				//il prossimo pilota che aggiungo: d deve avere un id maggiore dell'id del pilota aggiunto all'
				//iterata precedente:ultimoAggiunto . infatti ultimoAggiunto.compareTo(d) è <0 se id di ultimoAggiunto 
				//viene prima (è minore) di id di d 
				soluzioneParziale.add(d);//metto
				Ricorsivo(soluzioneParziale,livello+1,k,d);//provo
				soluzioneParziale.remove(d);//tolgo
			}
		}
	}
	private void cercaIdMaggiore(){
		int max=Integer.MIN_VALUE;
		for(Driver d :grafo.vertexSet()){
			if(d.getDriverId()>max)
				max=d.getDriverId();
		}
		idMaggiore=max;
	}
	private int PunteggioTeam(HashSet<Driver> team ){
		if(team.isEmpty())
			return Integer.MAX_VALUE;
		int punteggio=0;
		for(Driver d: grafo.vertexSet()){
			if(!team.contains(d)){
				for(Driver dteam:team){
					DefaultWeightedEdge arco=grafo.getEdge(d,dteam);
					if(arco!=null){
						punteggio=(int) (punteggio+grafo.getEdgeWeight(arco));
					}
				}
			}
			
		}
	return punteggio;	
	}
}
