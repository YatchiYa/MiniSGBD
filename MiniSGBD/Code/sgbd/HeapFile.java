package sgbd;
import rel.RelDef;

public class HeapFile {
	private RelDef relation;

	public RelDef getRelation() {
		return relation;
	}

	public void setRelation(RelDef relation) {
		this.relation = relation;
	}

	public HeapFile(RelDef relation) {
		
		this.relation = relation;
	}
	
	public  void createNewOnDisk () {
		
	}
}
