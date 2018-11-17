package rel;
import java.util.List;

import Schema.RelSchemaDef;

public class RelDef {
	
	private RelSchemaDef relDef;
	
	
	/**
	 * 
	 * @param relDef
	 */

	public RelDef(RelSchemaDef relDef) {
		super();
		this.relDef = relDef;
	}
	
	/**
	 * 
	 * @return
	 */
	public RelSchemaDef getRelDef() {
		return relDef;
	}

	/**
	 * 
	 * @param relDef
	 */
	public void setRelDef(RelSchemaDef relDef) {
		this.relDef = relDef;
	}



	
}
