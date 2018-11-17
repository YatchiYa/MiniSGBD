package sgbd;
import java.util.ArrayList;
import java.util.List;

import rel.RelDef;

public class DBDef {
	// verifier que c'est un singleton a faire !!!!!

	private static final DBDef INSTANCE = new DBDef();

	public static DBDef getInstance(){
	      return INSTANCE;
	}
	
	
	
	
	private ArrayList<RelDef> listRelDef;
	private int cptRel=0;
	
	
	
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


	// init and finish method
	public static void init() {
		
	}
	
	public static void finish() {
		
	}
	
	
	
}
