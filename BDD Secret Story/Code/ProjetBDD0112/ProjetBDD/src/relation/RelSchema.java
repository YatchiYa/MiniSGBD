package relation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Cette classe contient le schéma d’une relation donnée :<br>
 * - nom<br>
 * - nombre de colonnes<br>
 * - types des colonnes<br>
 * Pour les types, on se restreint aux entiers, float et string de taille fixe T (notés « stringT »)
 * On suppose que pour une relation à N colonnes, les colonnes sont nommées C1,…,CN
 *
 */
public class RelSchema implements Serializable{

	private String nom_rel;
	private int nb_col;
	private ArrayList<String> type_col;
	
	/**
	 * Construit une RelSchema à partir d'un nom de relation ainsi qu'un nombre de colonne
	 * @param nom_rel
	 * @param nb_col
	 */
	public RelSchema(String nom_rel, int nb_col) {
		this.nom_rel = nom_rel;
		this.nb_col = nb_col;
		type_col = new ArrayList<String>(nb_col);
	}
	
	public RelSchema() {
		this(null,0);
	}
	
	/**
	 * 
	 * @return le nom de la relation
	 */
	public String getNom_rel() {
		return nom_rel;
	}
	
	/**
	 * Modifie le nom de la relation par nom_rel
	 * @param nom_rel 
	 */
	public void setNom_rel(String nom_rel) {
		this.nom_rel = nom_rel;
	}

	/**
	 * 
	 * @return le nombre de colonne de la relation
	 */
	public int getNb_col() {
		return nb_col;
	}

	/**
	 * Modifie le nombre de colonne par nb_col
	 * @param nb_col
	 */
	public void setNb_col(int nb_col) {
		this.nb_col = nb_col;
	}

	/**
	 * 
	 * @return retourne les types des colonnes de la relation
	 */
	public ArrayList<String> getType_col() {
		return type_col;
	}
	
	/**
	 * Modifie la liste comportant les types des colonnes par une liste passée en parametre
	 * @param type_col
	 */
	public void setType_col(ArrayList<String> type_col) {
		this.type_col = type_col;
	}
}