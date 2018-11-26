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
	
	/**
	 * La HeaderPage aura donc la structure suivante :<br>
	 * - NbPagesDeDonnées : int<br>
	 * - NbPageDeDonnées entrées de la forme : (IdxPage : int ; NbSlotsRestantDisponibles: int)
	 * @throws IOException
	 */
	public void createNewOnDisk () throws IOException {
		DiskManager.CreateFile(relation.getFileIdx());	
	
		// ajout de la HeaderPage je suis pas sur !!!!
		DiskManager.AddPage(relation.getFileIdx(), 0);
		
	}
	
	
	/*public void getFreePageId(oPageId) {
		
	}
	*/
	
	/*public void updateHeaderWithTakenSlot(iPageId) {
		
	}
	*/
	
}
