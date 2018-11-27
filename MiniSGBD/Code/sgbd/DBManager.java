package sgbd;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Schema.RelSchemaDef;
import gestion.Commande;
import rel.RelDef;
import rel.Record;

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
		
		db.init();
		FileManager.init(db);
	}
	
	public void finish() throws FileNotFoundException, IOException {
		// db.finish();
		BufferManager.flushAll();
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
		
		
		int recordSize =0;
		/** on determine la taille d'un recordSize par rapport au TypeC 
			4 pour les int, 4 pour les float, et x*2 pour les stringx
		 **/
		for(String s :typeC) {
			switch (s.toLowerCase()) {
			case "int" :case"float" :recordSize+=4;
			break;		
			default :recordSize+= 2*Integer.parseInt(s.substring(6));  //on récupére le chiffre du StringT c'est le T
			}
		}
		//On determine le nombre de cases sur une page qui est calculable si 
		//on connaît la taille d’un record et la taille d’une page PageSize
		//et sachant que chaque case represente un octet dans la bytemap
		int slotCount= constants.Constants.pageSize/(recordSize+1);
		
		
		
		// creation de la relation avec la nouvelle rel
		RelDef relDef =new RelDef(new_Rel,db.getCptRel(),recordSize,slotCount);
		// ajouter a la base relation
		db.AddRelation(relDef);
		// incrementer le countRel  de la DBDef
		db.incrementCount();
		
		System.out.println("add relation Done");
		
		
	}


	// insert method !! 

	public static void insert(String nomRelation,ArrayList<String> listValeurs) throws IOException {
		Record r = new Record();
		r.setListValues(listValeurs);
		
		ArrayList<RelDef> list_rel = db.getListRelDef();
		RelDef relFind = null;
		boolean find = false;
		
		for(int i = 0;i<list_rel.size();i++) {
			String nameRel = list_rel.get(i).get(i).getNom_rel();
			if(nomRelation.equals(nameRel)) {
				relFind = list_rel.get(i);
				find = true;
				break;
			}
		}
		
		if(find) {
			HeapFile hf = new HeapFile(relFind);
			hf.insertRecord(r);
		}
		else {
			System.out.println("*** Erreur ! Ce nom de relation n'existe pas ! Veuillez ressayer. ***\n");
		}	
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
