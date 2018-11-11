package sgbd;

import relation.RelDef;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Cette classe contient une liste de RelDef et un entier = compteur de relations
 *
 */
public class DbDef implements Serializable{
	/**
	 * liste des relations de la db
	 */
	private ArrayList<RelDef> l;
	/**
	 * compteur de relation de la db
	 */
	private int count;
	
	public DbDef(int count) {
		this.count = count;
		l = new ArrayList<RelDef>(count);
	}
	
	public DbDef() {
		this(0);
	}
	
	/**
	 * retourne la liste des relations de la db
	 * @return
	 */
	public ArrayList<RelDef> getL() {
		return l;
	}

	/**
	 * Modifie la liste des relations par une liste passée en parametre
	 * @param l
	 */
	public void setL(ArrayList<RelDef> l) {
		this.l = l;
	}

	/**
	 * Retourne le compteur de relation
	 * @return
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Modifie le compteur par un entier passé en parametre
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * ajoute rd à la liste de relation
	 * @param rd
	 */
	public void ajouterRelation(RelDef rd) {
		l.add(rd);
	}
	
	/**
	 * incremente de +1 le compteur de relation
	 */
	public void incrementCount() {
		this.count++;
	}
	
	/**
	 * Reinitialise le db (compteur de relation à 0 et liste de relations vide)
	 */
	public void reset() {
		this.count = 0;
		this.l = new ArrayList<RelDef>(0);
	}
}
