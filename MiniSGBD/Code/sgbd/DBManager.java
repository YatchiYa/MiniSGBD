package sgbd;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Schema.RelSchemaDef;
import constants.Commande;
import rel.RelDef;

public class DBManager {
	Commande cmd= new Commande();
	
	private static final DBManager INSTANCE = new DBManager();
	
	public DBManager getInstance(){
	      return INSTANCE;
	   }
	
	public DBManager() {
		// constructor
	}
	

	private static DBDef db;
	
	
	public static void init() throws FileNotFoundException, ClassNotFoundException, IOException {
		// the init function
		// DBDef.getInstance();
		
		//creation de la dbDef
		db = new DBDef();
		
		db.init();
	}
	
	public void finish() throws FileNotFoundException, IOException {
		// db.finish();
		
	}
	
	public void processCommande(String commande) {
		cmd.listCommande(commande);
	}
	
	/**
	 * 
	 * @param NomRel
	 * @param nbC
	 * @param typeC
	 */
	
	// create relation
	public static void CreateRelation(String NomRel, int nbC, ArrayList<String> typeC) {
		RelSchemaDef new_Rel = new RelSchemaDef(NomRel, nbC);
		new_Rel.setType_col(typeC);
		
		// creation de la relation avec la nouvelle rel
		RelDef relDef = new RelDef(new_Rel);
		// ajouter a la base relation
		db.AddRelation(relDef);
		// incrementer le count de la base
		db.incrementCount();
		
		System.out.println("add relation Done");
		
		
	}

	
	
	public static DBDef getDb() {
		return db;
	}

	public static void setDb(DBDef db) {
		DBManager.db = db;
	}
	
}
