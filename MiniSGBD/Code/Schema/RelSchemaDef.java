package Schema;

import java.util.ArrayList;

public class RelSchemaDef {
	private String nom_rel;
	private int nb_col;
	private ArrayList<String> type_col;
	
	/**
	 * 
	 * @param nom_rel
	 * @param nb_col
	 * @param type_col
	 */
	public RelSchemaDef(String nom_rel, int nb_col) {
		super();
		this.nom_rel = nom_rel;
		this.nb_col = nb_col;
	}
	
	
	public RelSchemaDef() {
		this(null,0);
	}

	
	
	public String getNom_rel() {
		return nom_rel;
	}

	public void setNom_rel(String nom_rel) {
		this.nom_rel = nom_rel;
	}

	public int getNb_col() {
		return nb_col;
	}

	public void setNb_col(int nb_col) {
		this.nb_col = nb_col;
	}

	public ArrayList<String> getType_col() {
		return type_col;
	}

	public void setType_col(ArrayList<String> type_col) {
		this.type_col = type_col;
	}
	
	
	
	
}
