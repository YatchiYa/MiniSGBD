package menu;

import relation.Record;
import relation.RelSchema;
import sgbd.GlobalManager;
import sgbd.HeaderPageInfo;
import sgbd.HeapFile;
import sgbd.PageId;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import index.Arbre;
import index.Index;
import index.Noeud;
import index.UtilityBPLusTree;

/**
 * Utilitaire permettant les différents format d'affichage pour chaque commande
 *
 */
public class Utilities {
	/**
	 * lien vers le dossier ./DB/
	 */
	private static final String LIEN = "."+File.separatorChar+"DB"+File.separatorChar;
	private static List<String> commandAutorisee = Arrays.asList("create","insert","fill","selectall","select","join","createindex","selectindex","joinindex","clean","exit","help");
	
	public static String affichageMenu() throws IOException {
		
		String command;
		String action;
		boolean commandVerif;
		
		do {
			command = Saisie.lireChaine("mini-sgbd => ");
			StringTokenizer st = new StringTokenizer(command," ");
			action = st.nextToken();
			commandVerif = commandAutorisee.contains(action);
			if(!commandVerif) {
				System.out.println("*** Commande invalide ! Tapez 'help' pour de l'aide. ***\n");
			}
		}while (!commandVerif);
		
		switch(action) {
			case "create" : actionCreate(command);
			break;
				
			case "insert" : actionInsert(command);	
			break;
				
			case "fill" : actionFill(command);
			break;
				
			case "selectall" : actionSelectAll(command); 
			break;
			
			case "select" : actionSelect(command); 
			break;
			
			case "createindex" : actionCreateIndex(command);
			break;
			
			case "selectindex" : actionSelectIndex(command);
			break;
				
			case "clean" : actionClean(command);
			break;
			
			case "join" : actionJoin(command);
			break;
				
			case "joinindex" : actionJoinIndex(command);
			break;
			
			case "exit" : actionExit(command);
			break;
			
			case "help" : actionHelp(command);
			break;
				
			default : System.out.println("*** Erreur ! Cette commande n'existe pas ! ***\n");
			break;
		}
		return command;
	}

	public static void actionHelp(String command) {
		System.out.println("Vous utilisez un mini-sgbd, voici toutes les commandes disponibles.");
		System.out.println("\thelp -> pour de l'aide sur les commandes");
		System.out.println("\tcreate -> create RelName NbCol TypeCol[1] TypeCol[2] ... TypeCol[NbCol]");
		System.out.println("\tinsert -> insert RelName Val[1] Val[2] ... Val[n]");
		System.out.println("\tfill -> fill RelName nomfichier.csv");
		System.out.println("\tselectall -> selectall RelName");
		System.out.println("\tselect -> select RelName indiceCol v");
		System.out.println("\tjoin -> join RelName1 RelName2 indiceCol1 indiceCol2");
		System.out.println("\tcreateindex -> createindex RelName indiceCol val_ordre");
		System.out.println("\tselectindex -> selectindex RelName indiceCol v");
		System.out.println("\tjoinindex -> joinindex RelName1 RelName2 indiceCol1 indiceCol2");
		System.out.println("\tclean -> pour vider la base de données");
		System.out.println("\texit -> pour quitter");
		
	}

	public static void actionJoinIndex(String command) throws IOException {
		
		StringTokenizer st = new StringTokenizer(command.substring(10), " ");
		String nomRelation1 = st.nextToken();
		String nomRelation2 = st.nextToken();
		int indiceCol1 = Integer.parseInt(st.nextToken());
		int indiceCol2 = Integer.parseInt(st.nextToken());
		
		//Recuperer la list des heapfile puis chercher le nom de la rel
		ArrayList<HeapFile> list = GlobalManager.getListeHeapFile();
		HeapFile relFind1 = findHeapFile(list,nomRelation1);
		HeapFile relFind2 = findHeapFile(list,nomRelation2);
		
		if(relFind1 != null && relFind2 != null) {
			//relation2 = relation interne possédant un index
			ArrayList<Index> listIndex = GlobalManager.getListeIndex();
			Index indexFind = findIndex(listIndex, nomRelation2, indiceCol2);
			
			if(indexFind != null) {
				int totalRecordPrinted = 0;
				
				//on recupere le type de la colonne de la relation 2 (qui possede un index)
				String type = relFind2.getRel().getrS().getType_col().get(indiceCol2-1);
				
				//on recupere l'arbre qui est dans l'index de la relation 2
				Arbre arbre = indexFind.getArbre();
				
				//on recupere les fileid des heapfiles
				int fileIdHP = relFind1.getRel().getFileId();
				
				//on recupere la header page des heapfiles
				HeaderPageInfo hpi1 = new HeaderPageInfo();
				relFind1.getHeaderPageInfo(hpi1);
				
				ArrayList<Integer> listIdxPage1 = hpi1.getIdxPageTab();
				//boucle de la relation1
				for(int i=0; i<listIdxPage1.size(); i++) {
					//on parcourt chaque page de la relation 1
					int idxPageCourante1 = listIdxPage1.get(i).intValue();
					PageId pageCourante1 = new PageId(fileIdHP, idxPageCourante1); 
					
					ArrayList<Record> listRecords1 = relFind1.getAllRecordsPage(pageCourante1);
						
					//pour chaque records, on parcourt toutes les feuilles de l'arbre et on regarde si la valeur est presente dans l'index
					for(int j=0; j<listRecords1.size(); j++) {
						String val = listRecords1.get(j).getListValues().get(indiceCol1-1);
						//on retourne le record trouvé dans l'index
						Record r = UtilityBPLusTree.getRecordInBtree(arbre.getRacine(),val, type, relFind2);
									
						if(r != null) {
							System.out.println(listRecords1.get(j) + r.toString());
							totalRecordPrinted++;
						}	
					}
				}
				System.out.println("Total records : " + totalRecordPrinted);
			}
			else {
				System.out.println("*** L'index n'existe pas ! ***\n");
			}
		}
		else {
			System.out.println("*** Une des 2 relations n'existe pas dans la base de données ! ***\n");
		}
	}

	public static void actionJoin(String command) {
		
		StringTokenizer st = new StringTokenizer(command.substring(4), " ");
		String nomRelation1 = st.nextToken();
		String nomRelation2 = st.nextToken();
		int indiceCol1 = Integer.parseInt(st.nextToken());
		int indiceCol2 = Integer.parseInt(st.nextToken());
		
		//Recuperer la list des heapfile puis chercher le nom de la rel
		ArrayList<HeapFile> list = GlobalManager.getListeHeapFile();
		HeapFile relFind1 = findHeapFile(list,nomRelation1);
		HeapFile relFind2 = findHeapFile(list,nomRelation2);
		
		if(relFind1 != null && relFind2 != null) {
			try {
				relFind1.join(relFind2, indiceCol1, indiceCol2);
				
			}catch(IOException e) {
				System.out.println("Une erreur s'est produite lors de la jointure !");
				System.out.println("Détails : " + e.getMessage());
			}
			
		}
		else {
			System.out.println("*** Une des 2 relations n'existe pas dans la base de données ! ***\n");
		}
		
	}

	public static void actionSelect(String command) {
		
		StringTokenizer st = new StringTokenizer(command.substring(7), " ");
		String nomRelation = st.nextToken();
		int indiceCol = Integer.parseInt(st.nextToken());
		String condition = st.nextToken();
		
		//Recuperer la list des heapfile puis chercher le nom de la rel
		ArrayList<HeapFile> list = GlobalManager.getListeHeapFile();
		HeapFile relFind = findHeapFile(list,nomRelation);
		
		if(relFind != null) {
			try {
				relFind.printAllRecordsWithFilter(indiceCol, condition);
			}catch(IOException e) {
				System.out.println("Une erreur s'est produite lors de l'affichage des records !");
				System.out.println("Détails : " + e.getMessage());
			}
		}
		else {
			System.out.println("*** \"" +nomRelation + "\" n'existe pas dans la base de données ! ***\n");
		}
	}

	public static void actionCreateIndex(String command) {
	
		StringTokenizer st = new StringTokenizer(command.substring(12), " ");
		String nomRelation = st.nextToken();
		int indiceCol = Integer.parseInt(st.nextToken());
		int ordreArbre = Integer.parseInt(st.nextToken());
		
		//Recuperer la list des heapfile puis chercher le nom de la rel
		ArrayList<HeapFile> list = GlobalManager.getListeHeapFile();
		HeapFile relFind = findHeapFile(list, nomRelation);
		
		if(relFind !=null) {
			try {
				String type = relFind.getRel().getrS().getType_col().get(indiceCol-1);
				
				ArrayList<Record> listRecords = relFind.getAllRecords();
				
				//La relation est vide, elle ne contient aucun records
				if(listRecords.size() == 0) {
					System.out.println("*** Impossible de créer l'index ! Relation vide ! ***\n");
				}
				else {
					UtilityBPLusTree.sortRecord(listRecords,indiceCol,type);
					
					ArrayList<Noeud> listeFeuilles = UtilityBPLusTree.getListeFeuilles(listRecords,ordreArbre,indiceCol);
					
					Arbre arbre = new Arbre(ordreArbre);
					
					UtilityBPLusTree.bulkLoading(arbre,listeFeuilles);
					
					GlobalManager.addIndex(new Index(nomRelation, indiceCol, arbre));
				}
			
			}catch(IOException e) {
				System.out.println("Une erreur est survenue lors de la récupération des records !");
				System.out.println("Details : " + e.getMessage());
			}
		}
		else {
			System.out.println("*** \"" +nomRelation + "\" n'existe pas dans la base de données ! ***\n");
		}
	}

	public static void actionSelectIndex(String command) {
		
		StringTokenizer st = new StringTokenizer(command.substring(12), " ");
		String nomRelation = st.nextToken();
		int indiceCol = Integer.parseInt(st.nextToken());
		String param = st.nextToken();
		
		//Recuperer la list des heapfile puis chercher le nom de la rel
		ArrayList<HeapFile> list = GlobalManager.getListeHeapFile();
		HeapFile relFind = findHeapFile(list, nomRelation);
		
		if(relFind !=null) {
			ArrayList<Index> listIndex = GlobalManager.getListeIndex();
			Index indexFind = findIndex(listIndex, nomRelation, indiceCol);
			
			if(indexFind != null) {
				String type = relFind.getRel().getrS().getType_col().get(indiceCol-1);
				
				Arbre arbre = indexFind.getArbre();
				
				try{
					UtilityBPLusTree.selectInBtree(arbre.getRacine(), param, type, relFind);
				}catch(IOException e) {
					System.out.println("Une erreur est survenue lors de la récupération des records avec l'index !");
					System.out.println("Details : " + e.getMessage());
				}
			}
			else {
				System.out.println("*** Aucun index n'a été pour cette relation ou cette colonne ! ***\n");
			}
		}
		else {
			System.out.println("*** \"" +nomRelation + "\" n'existe pas dans la base de données ! ***\n");
		}
	}
	
	public static void actionCreate(String command){
		
		RelSchema relation = new RelSchema();
	
		try {
			//command.substring(7) correspond à tous les parametres après "create"
			//c'est a dire RelName NbCol TypeCol[1] TypeCol[2] ... TypeCol[NbCol]
			relation = extractRel(command.substring(7));
		}catch(IllegalArgumentException e) {
			System.out.println("\n*** " + e.getMessage()+ " ***\n");
		}
		GlobalManager.createRelation(relation.getNom_rel(), relation.getNb_col(), relation.getType_col());
	}
	
	public static void actionFill(String command) {
		
		StringTokenizer commande = new StringTokenizer(command.substring(5)," ");
		String nomRel = commande.nextToken();
		String nomFichier = commande.nextToken();
		
		ArrayList<String> contenuCSV = null;
		
		try {
			contenuCSV = extractContenuCSV(nomFichier);
			try{
				extractRecords(contenuCSV,nomRel);
			}catch(IOException e) {
				System.out.println("Une erreur est survenue !");
				System.out.println("Détails : " + e.getMessage());
			}
		}catch(IOException e) {
			System.out.println("Une erreur est survenue !");
			System.out.println("Détails : " + e.getMessage());
		}
	}
	
	public static void actionClean(String command) {
		
		try {
			GlobalManager.clean();
		}catch(Exception e) {
			System.out.println("Une erreur est survenue lors de la suppression ! ");
			System.out.println("Détails : " + e.getMessage());
		}
	}
	
	public static void actionInsert(String command) {
		
		StringTokenizer st = new StringTokenizer(command," ");
		st.nextToken();
		String name = st.nextToken();
		ArrayList<String> l = new ArrayList<String>(0);
		while(st.hasMoreTokens()) {
			l.add(st.nextToken());
		}
		try {
			GlobalManager.insert(name, l);
		}catch(IOException e) {
			
		}
	}
	
	public static void actionSelectAll(String command) {
		
		String nomRelation = command.substring(10);
		//Recuperer la list des heapfile puis chercher le nom de la rel
		ArrayList<HeapFile> list = GlobalManager.getListeHeapFile();
		HeapFile relFind = findHeapFile(list,nomRelation);
		
		if(relFind != null) {
			try {
				relFind.printAllRecords();
			}catch(IOException e) {
				System.out.println("Une erreur s'est produite lors de l'affichage des records !");
				System.out.println("Détails : " + e.getMessage());
			}
		}
		else {
			System.out.println("*** \"" +nomRelation + "\" n'existe pas dans la base de données ! ***\n");
		}
	}
	
	public static void actionExit(String command) {
		try {
			GlobalManager.finish();
		}catch(IOException e) {
			System.out.println("Une erreur est survenue !");
			System.out.println("Détails : " + e.getMessage());
		}
		System.out.println("\nAu revoir !");
	}
	
	/**
	 * Cette méthode permet de créer une relation
	 * @param chaine correspond à la commande de l'user
	 * @return retourne une relation du type RelSchema
	 */
	public static RelSchema extractRel(String chaine) throws IllegalArgumentException{
		//commande de l'user
		String commande = chaine.trim();
		
		//on récupère tous les mots séparés par un espace dans la commande de l'user
		StringTokenizer st = new StringTokenizer(commande," ");
		
		RelSchema relation = new RelSchema();
		ArrayList<String> typeCol = new ArrayList<String>(0);
		
		//on rempli le nom de la relation
		String nom = st.nextToken();
		relation.setNom_rel(nom);
		//on rempli le nombre de colonne que la relation possede
		int nbCol = Integer.parseInt(st.nextToken());
		relation.setNb_col(nbCol);
		//on rempli les types des colonnes de la relation
		while (st.hasMoreTokens()) {
			//a faire : verifier le type des colonnes (uniquement int, float, string avec taille)
			String type = st.nextToken().toLowerCase();
			if(type.equals("int") || type.equals("float") || type.contains("string")) {
				typeCol.add(type);
			}
			else {
				throw new IllegalArgumentException("Ce type n'est pas autorisé !");
			}
		}
		relation.setType_col(typeCol);
		
		return relation;
	}
	
	public static ArrayList<String> extractContenuCSV(String nomFichier) throws IOException{
		//il faut que le fichier csv soit dans ce dossier "."+File.separatorChar+"DB"+File.separatorChar
		ArrayList<String> contenuCSV = new ArrayList<String>();
        try(FileReader fr = new FileReader(LIEN + nomFichier);BufferedReader br = new BufferedReader(fr)){
        	String ligne;
        	do{
        		ligne = br.readLine();
        		if(ligne != null) {
        			contenuCSV.add(ligne);
        		}
        	}while(ligne != null);
        }catch(IOException e) {
        	System.out.println("Problème de lecture : " + e.getMessage());
        }
        return contenuCSV;
	}
	
	public static void extractRecords(ArrayList<String> contenuCSV, String nomRel) throws IOException {
		for(int i = 0; i<contenuCSV.size(); i++) {
			String ligne = contenuCSV.get(i);
			StringTokenizer st = new StringTokenizer(ligne, ",");
			
			ArrayList<String> listValeurs = new ArrayList<String>(0);
			
			while(st.hasMoreTokens()) {
				listValeurs.add(st.nextToken());
			}
			
			GlobalManager.insert(nomRel, listValeurs);
		}
	}
	
	public static HeapFile findHeapFile(ArrayList<HeapFile> list,String nomRelation) {
		HeapFile relFind = null;
		
		for(int i =0 ;i<list.size();i++) {
			String nomRelCourant = list.get(i).getRel().getrS().getNom_rel();
			HeapFile relCourant = list.get(i);
			if(nomRelCourant.equals(nomRelation)) {
				relFind = relCourant;
			}
		}
		return relFind;
	}
	
	public static Index findIndex(ArrayList<Index> list, String nomRelation, int numCol) {
		Index indexFind = null;
		
		for(int i =0 ;i<list.size();i++) {
			String nomRelCourant = list.get(i).getNomRel();
			int numColCourant = list.get(i).getNumCol();
			Index indexCourant = list.get(i);
			
			if(nomRelCourant.equals(nomRelation) && numCol == numColCourant) {
				indexFind = indexCourant;
			}
		}
		return indexFind;
	}
}
