package sgbd;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Schema.RelSchemaDef;
import constants.Commande;
import constants.Constants;
import rel.RelDef;

public class DBManager {

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
		db.finish();

	}

	public void processCommande(String commande) {
		Commande.listCommande(commande);
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
		int slotCount= Constants.pageSize/(recordSize+1);

		// une RelDef nouvellement construite avec fileIdx 
		// creation de la relation avec la nouvelle rel
		RelDef relDef =new RelDef(new_Rel,db.getCptRel(),recordSize,slotCount);

		// ajouter a la base relation
		db.AddRelation(relDef);
		// incrementer le countRel  de la DBDef
		db.incrementCount();

	//	System.out.print("add relation Done");


	}



	public static DBDef getDb() {
		return db;
	}

	public static void setDb(DBDef db) {
		DBManager.db = db;
	}

}
