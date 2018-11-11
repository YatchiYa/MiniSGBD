package sgbd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import index.Index;
import relation.Record;
import relation.RelDef;
import relation.RelSchema;

public class GlobalManager{
	
	private static final String LIEN = "."+File.separatorChar+"DB"+File.separatorChar+"Catalog.def";
	private static final int K = 4096;
	
	private static DbDef db;
	private static ArrayList<HeapFile> listeHeapFile;
	private static ArrayList<Index> listeIndex;
	
	/**
	 * Après la création de l’instance DbDef, la méthode «remplit» 
	 * cette instance avec le contenu du fichier Catalog.def (si un tel fichier existe!)<br>
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void init() throws FileNotFoundException, IOException, ClassNotFoundException { 
		db = new DbDef();
		
		File fichier =  new File(LIEN);
		if(fichier.exists()) {
			// ouverture d'un flux sur un fichier
			try(FileInputStream fis = new FileInputStream(fichier);ObjectInputStream ois =  new ObjectInputStream(fis);){
				 // désérialization de l'objet
				db = (DbDef)ois.readObject();
			}
		}
		//refresh la liste de heapfiles
		refreshHeapFiles();
		//initialisation de la liste d'indexs
		listeIndex = new ArrayList<Index>(0);
	}
	
	/**
	 * Efface tous les fichiers de données courants (les data_x.rf) et le fichier catalogue, 
	 * et qui «remet à vide» la DBDef, la liste des heap files et la liste d'index
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public static void clean() throws FileNotFoundException, ClassNotFoundException, IOException {
		//reinitialise le bufferPool et écrit les buffers sur disque si besoin
		BufferManager.flushAll();
		
		//suppression du fichier du catalog.def
		File catalogDef = new File(LIEN);
		if(catalogDef.delete()) {
			System.out.println("Suppression ... \"Catalog.def\" supprimé avec succès !");
		}
		else {
			System.out.println("*** Aucun fichier \"Catalog.def\" présent ! ***\n");
		}
		
		//suppression des relations
		for(int i = 0; i<listeHeapFile.size(); i++) {
			File dataRf = new File("."+File.separatorChar+"DB"+File.separatorChar+"Data_"+i+".rf");
			if(dataRf.delete()) {
				System.out.println("Suppression ... Relation supprimée !");
			}
		}
		
		//on reinitialise le db en appelant sa methode reset
		db.reset();
		//on "vide" les listes de heapfile et d'index
		listeHeapFile = new ArrayList<HeapFile>(0);
		listeIndex = new ArrayList<Index>(0);
		
		System.out.println("Suppression ... La base de données a été supprimée avec succès !\n");
	}
	
	/**
	 * Parcourt la liste des RelDef dans le DbDef et créé, pour chaque RelDef, un HeapFile correspondanr qui est rajouté ensuite à la liste de heapfile du GlobalManager.
	 */
	public static void refreshHeapFiles() {
		listeHeapFile = new ArrayList<HeapFile>(0);
		for(RelDef r : db.getL()) {
			listeHeapFile.add(new HeapFile(r));
		}
	}
	
	/**
	 * Elle enregistre dans le fichier Catalog.def (placé dans le dossier DB) le contenu de l’unique instance DbDef du SGBD.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void finish() throws FileNotFoundException, IOException {
		File fichier =  new File(LIEN);

		 // ouverture d'un flux sur un fichier
		try(FileOutputStream fos = new FileOutputStream(fichier);ObjectOutputStream oos =  new ObjectOutputStream(fos);){
			// sérialization de l'objet
			oos.writeObject(db);
		}
		BufferManager.flushAll();
	}
	
	public static void insert(String nomRelation,ArrayList<String> listValeurs) throws IOException {
		Record r = new Record();
		r.setValue(listValeurs);
		
		ArrayList<RelDef> listRelation = db.getL();
		RelDef relFind = null;
		boolean find = false;
		
		for(int i = 0;i<listRelation.size();i++) {
			String nameRel = listRelation.get(i).getrS().getNom_rel();
			if(nomRelation.equals(nameRel)) {
				relFind = listRelation.get(i);
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
	
	/**
	 * Cette méthode s'occupe de : <br/>
	 * - la création d’une structure RelDef avec comme FileId le compteur courant du DbDef <br/>
	 * - le rajout de la RelDef à la liste dans le DbDef <br/>
	 * - l’incrémentation du compteur de relations du DbDef <br/>
	 * - la création (via le gestionnaire disque) du fichier correspondant à la relation <br/>
	 * @param nomRelation
	 * @param nombreColonnes
	 * @param typesDesColonnes
	 * @throws IOException
	 */
	public static void createRelation(String nomRelation, int nombreColonnes, ArrayList<String> typesDesColonnes) {
		RelSchema nouvelle_relation = new RelSchema(nomRelation, nombreColonnes);
		nouvelle_relation.setType_col(typesDesColonnes);
		
		int sizeRec = 0;
		//il faut déterminer la taille d'un record en fonction des types des colonnes
		//int = 4 octets / float = 4 octets / stringx = 2*x octets
		for(String s : typesDesColonnes) {
			switch(s.toLowerCase()) {
				case "int" : case "float" : sizeRec += 4;
				break;
				//il s'agit du stringT
				default : sizeRec += 2*Integer.parseInt(s.substring(6));
			}
		}
		//il faut déterminer le nombre total de cases (slot) sur une page sachant
		//que pour chaque case il y a un octet correspondant dans la bitmap (nbTotalCase*1octet).
		int slotCount = K/(sizeRec+1);
		
		//la création d'une structure RelDef avec comme FileId le compteur courant du DbDef
		RelDef rd = new RelDef(nouvelle_relation,db.getCount(),sizeRec,slotCount);
		
		//le rajout de la RelDef à la liste dans le DbDef
		db.ajouterRelation(rd);
		//l'incrémentation du compteur de relations du DbDef
		db.incrementCount();
		
		//la création (via le gestionnaire disque) du fichier correspondant à la relation.
		try {
			DiskManager.createFile(rd.getFileId());
		}catch(IOException e) {
			System.out.println("*** Une ereur s'est produite lors de la création du fichier ! ***");
			System.out.println("Détails : " + e.getMessage());
		}
		
		//creation d'un heapfile (pointant vers la RelDef nouvellement créée) et rajout de ce heapfile
		//dans la liste des heapfiles
		HeapFile hf = new HeapFile(rd);
		listeHeapFile.add(hf);
		//appel de create header sur le heapfile nouvellement créé
		try {
			hf.createHeader();
		}catch(IOException e) {
			System.out.println("*** Une ereur s'est produite lors de la création de la header page ! ***");
			System.out.println("Détails : " + e.getMessage());
		}
	}
	
	public static DbDef getDB(){
		return db;
	}
	
	public static ArrayList<HeapFile> getListeHeapFile(){
		return listeHeapFile;
	}
	
	public static ArrayList<Index> getListeIndex(){
		return listeIndex;
	}
	
	public static void addIndex(Index index) {
		listeIndex.add(index);
	}
}
