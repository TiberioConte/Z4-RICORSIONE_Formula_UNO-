package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.DriverIdMap;
import it.polito.tdp.formulaone.model.Season;
import it.polito.tdp.formulaone.model.StrutturaPerArcoOttimizzato;
import it.polito.tdp.formulaone.model.StrutturaPerArcoSemiOttimizzato;


public class FormulaOneDAO {

	public ArrayList<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			ArrayList<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public ArrayList<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			ArrayList<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}

	public ArrayList<Driver> getTuttiIPIlotiHannoFinitoGaraNella(Season s, DriverIdMap mappaPiloti) {
		String sql = "select distinct (drivers.driverId),drivers.driverRef,drivers.number,drivers.code,drivers.forename, "
				+ "drivers.surname,drivers.dob,drivers.nationality,drivers.url "
				+ "from drivers,races,results "
				+ "where races.year=? and results.raceId=races.raceId and results.driverId=drivers.driverId and results.position is not null";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getYear().getValue());

			ResultSet rs = st.executeQuery();
			

			ArrayList<Driver> piloti = new ArrayList<>();
			while (rs.next()) {
				Driver d=mappaPiloti.get(rs.getInt("drivers.driverId"));
				if(d==null){
					d=new Driver(rs.getInt("drivers.driverId"),rs.getString("drivers.driverRef"),rs.getInt("drivers.number"),
							rs.getString("drivers.code"),rs.getString("drivers.forename"),rs.getString("drivers.surname"),
							rs.getDate("drivers.dob").toLocalDate(),rs.getString("drivers.nationality"),rs.getString("drivers.url"));
					mappaPiloti.put(d);
				}
				piloti.add(d);
			}

			conn.close();
			return piloti;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	public ArrayList<StrutturaPerArcoOttimizzato> getArchiOttimizzati(Season s, DriverIdMap mappaPiloti) {
		String sql = "select count(races.raceId) as numeroVittorie ,r1.driverId as d1 ,r2.driverId  as d2 "+
					"from races,results as r1,results as r2 "+
					"where races.raceId=r1.raceId and r1.position is not null and races.raceId=r2.raceId and r2.position is not null  and races.year=? and r1.position<r2.position "+
					"group by d1,d2 ";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getYear().getValue());

			ResultSet rs = st.executeQuery();
			

			ArrayList<StrutturaPerArcoOttimizzato> archi = new ArrayList<StrutturaPerArcoOttimizzato>();
			while (rs.next()) {
				
				Driver vincente =mappaPiloti.get(rs.getInt("d1"));
				Driver perdente =mappaPiloti.get(rs.getInt("d2"));
				
				if (vincente==null||perdente==null) {
					throw new IllegalArgumentException("ERRORE! Lo studente non è presente!");
				}
				
				StrutturaPerArcoOttimizzato arco= new StrutturaPerArcoOttimizzato(vincente,perdente,rs.getInt("numeroVittorie"));
				
				archi.add(arco);
			}

			conn.close();
			return archi;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
		
	}
	public ArrayList<StrutturaPerArcoSemiOttimizzato> getArchiSemiOttimizzati(Season s, DriverIdMap mappaPiloti, Driver pilota) {
		String sql = "select count(races.raceId) as numeroVittorie ,r2.driverId  as d2 "+
				     "from races,results as r1,results as r2 "+
				     "where races.raceId=r1.raceId and r1.position is not null and races.raceId=r2.raceId and r2.position is not null  and races.year=? and r1.position<r2.position and r1.driverId=? "+
				     "group by d2 ";

	try {
		Connection conn = DBConnect.getConnection();

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, s.getYear().getValue());
		st.setInt(2, pilota.getDriverId());

		ResultSet rs = st.executeQuery();
		

		ArrayList<StrutturaPerArcoSemiOttimizzato> archi = new ArrayList<StrutturaPerArcoSemiOttimizzato>();
		while (rs.next()) {
			
			
			Driver perdente =mappaPiloti.get(rs.getInt("d2"));
			
			if (perdente==null) {
				throw new IllegalArgumentException("ERRORE! Lo studente non è presente!");
			}
			
			StrutturaPerArcoSemiOttimizzato arco= new StrutturaPerArcoSemiOttimizzato(perdente,rs.getInt("numeroVittorie"));
			
			archi.add(arco);
		}

		conn.close();
		return archi;
	} catch (SQLException e) {
		e.printStackTrace();
		throw new RuntimeException("SQL Query Error");
	}
	}

	public int getArchiNonOttimizzati(Season s, DriverIdMap mappaPiloti, Driver vincente, Driver perdente) {
		String sql = "select count(races.raceId) as cnt "+
					"from results as r1,results as r2,races "+
					"where r1.raceId=r2.raceId and races.raceId=r1.raceId and r1.driverId=? and r2.driverId=? "+
					"and races.year=? and r1.position<r2.position and r1.position is not null and r2.position is not null";

	try {
		Connection conn = DBConnect.getConnection();

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, vincente.getDriverId());
		st.setInt(2, perdente.getDriverId());
		st.setInt(3, s.getYear().getValue());

		ResultSet rs = st.executeQuery();
		
		int numeroVittorie=0;
		if(rs.next()) {
			numeroVittorie=rs.getInt("cnt");
		}

		conn.close();
		return numeroVittorie;
	} catch (SQLException e) {
		e.printStackTrace();
		throw new RuntimeException("SQL Query Error");
	}
		
	}
	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

		List<Constructor> constructors = dao.getAllConstructors();
		System.out.println(constructors);
		
	}

	

	

	
	
}
