package sgbd;
import java.io.File;
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
	
	
	private static DBDef db;
	private static FileManager file;
	private static ArrayList<HeapFile> listeHeapFile;

	public DBManager() {
		// constructor
	}
	
	public static void init() throws FileNotFoundException, ClassNotFoundException, IOException {
		// the init function
		// DBDef.getInstance();
		
		//creation de la dbDef
		db = new DBDef();
		file = new FileManager();
		
		db.init();
		file.init(db);
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


	// insert method !! 
	public static void insert(String nom_rel,ArrayList<String> valeurs) throws IOException {
		
		
	}
	
	

	// clean method 	
	
	public static void clean() throws FileNotFoundException, ClassNotFoundException, IOException {
		
		//reinitialise le bufferPool et écrit les buffers sur disque si besoin
		BufferManager.flushAll();
		

		File catalogDef = new File(constants.Constants.catalogRep);
		
		// trying to delete the file catalog
		try {
			catalogDef.delete();
		}catch(Exception e) {
			System.out.println(" erreur de suppression de fichier catalog " + e);
		}
		
		
		//suppression des relations
		for(int i = 0; i< listeHeapFile.size(); i++) {		// heap file pas encore defeni !!!! 
			
			File dataRf = new File(constants.Constants.PATH + i + ".rf");
			try {
				dataRf.delete();
				System.out.println("Suppression ... Relation supprimée !");
			}catch(Exception e) {
				System.out.println(" erreur de suppression de la relation : " + e);
			}
		}
		
		// fonction reset defini dans la DBDef pour remetre le compteur a zero.
		db.reset();
		
		// on vide la list heap file ----> a faire 
		
		
		
		
		// message d'affichage final 
		System.out.println("DATA BASE deleted !!!");
			
	}
	
	
	
	
	
	public static DBDef getDb() {
		return db;
	}

	public static void setDb(DBDef db) {
		DBManager.db = db;
	}
	
}
