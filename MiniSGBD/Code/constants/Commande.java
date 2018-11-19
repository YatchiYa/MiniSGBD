package constants;

import java.util.ArrayList;
import java.util.StringTokenizer;

import Schema.RelSchemaDef;
import sgbd.DBManager;

public class Commande {
	
	
	public static String listCommande(String c) {		
		System.out.println("listCommande \n");
		String action;
		
		StringTokenizer st = new StringTokenizer(c," ");
		action = st.nextToken();
		
		switch(action) {
		
		case "create":
			System.out.println("create methode");
			createRel(c);
			break;
		default: 
			System.out.println("don t know the commande ");
			break;
		}
		
		return c;
	}
	
	public static void createRel(String commande) {
		
		RelSchemaDef n_rel = new RelSchemaDef();
		
		try{

			n_rel = getRelationAdded(commande.substring(7));
			
		}catch(IllegalArgumentException e) {
			System.out.println(e);
		}
		
		DBManager.CreateRelation(n_rel.getNom_rel(), n_rel.getNb_col(), n_rel.getType_col());
		System.out.print("done");
		
	}
	
	public static RelSchemaDef getRelationAdded(String chaine) {
		String commande = chaine.trim();
		
		// on essaye de recuperer les mots separer par un espace entre les mots
		StringTokenizer st = new StringTokenizer(commande," ");
		
		RelSchemaDef relation = new RelSchemaDef();

		ArrayList<String> typeCol = new ArrayList<String>(0);

		// le nom de la relation
		String nom = st.nextToken();
		relation.setNom_rel(nom);
		
		//nombre de colone de la relation
		int nbCol = Integer.parseInt(st.nextToken());
		relation.setNb_col(nbCol);
		
		while (st.hasMoreTokens()) {
			String type = st.nextToken().toLowerCase();
			if(type.equals("int") || type.equals("float") || type.contains("string")) {
				typeCol.add(type);
			}
			else {
				System.out.println("doesn t much");
			}
		}

		relation.setType_col(typeCol);
		return relation;
		
	}
}
