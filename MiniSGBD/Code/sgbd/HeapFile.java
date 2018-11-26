package sgbd;
import java.io.IOException;

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
	
	
	/*public void createNewOnDisk () throws IOException {
		DiskManager.CreateFile(relation.getFileIdx());	
	}
	*/
	
	public void getFreePageId(PageId oPageId) {
		int idxFile = relation.getFileIdx();
		
		
		
	}
}
	
	
	/*public void updateHeaderWithTakenSlot(iPageId) {
		
	}
	*/
	

