package sgbd;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import constants.Constants;
import index.Index;
import rel.RelDef;

public class DBDef {
	// verifier que c'est un singleton a faire !!!!!

	private static final DBDef INSTANCE = new DBDef();

	public static DBDef getInstance(){
	      return INSTANCE;
	}
	
	
	
	private ArrayList<RelDef> listRelDef;
	private int cptRel=0;
	private static DBDef db;
	
	
	
	public DBDef() {
		super();
	}

	/**
	 * 
	 * @param rel
	 */
	public void AddRelation(RelDef rel) {
		listRelDef.add(rel);
	}
	
	/**
	 * 
	 */
	public void incrementCount() {
		this.cptRel++;
	}

	/**
	 * 
	 */
	public void reset() {
		this.cptRel = 0;
		this.listRelDef = new ArrayList<RelDef>(0);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<RelDef> getListRelDef() {
		return listRelDef;
	}


	public void setListRelDef(ArrayList<RelDef> listRelDef) {
		this.listRelDef = listRelDef;
	}


	public int getCptRel() {
		return cptRel;
	}


	public void setCptRel(int cptRel) {
		this.cptRel = cptRel;
	}

	
	/**
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void finish() throws FileNotFoundException, IOException {
		
		File fichier =  new File(Constants.repositoryDB);

		 // ouverture d'un flux sur un fichier
		try(
				FileOutputStream fos = new FileOutputStream(fichier);
				ObjectOutputStream oos =  new ObjectOutputStream(fos);){
			// sérialization de l'objet
			oos.writeObject(db);
		}
		
		BufferManager.flushAll();
	}
	
	
	/**
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void init() throws FileNotFoundException, IOException, ClassNotFoundException { 
		db = new DBDef();
		
		File fichier =  new File(Constants.repositoryDB);
		if(fichier.exists()) {
			// ouverture d'un flux sur un fichier
			try(
					FileInputStream fis = new FileInputStream(fichier);
					ObjectInputStream ois =  new ObjectInputStream(fis);){


				db = (DBDef)ois.readObject();
			}
		}
	}
	
	
}
