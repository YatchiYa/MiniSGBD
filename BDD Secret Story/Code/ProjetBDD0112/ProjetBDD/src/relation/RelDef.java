package relation;

import java.io.Serializable;

/**
 * Cette classe contient toutes les informations associées à une relation.<br>
 * Ces informations comprendront un champ de type RelSchema, un entier FileId,<br>
 * un entier qui represente la taille d'un record et un entier qui correspond au nombre de case
 * de la relation.
 *
 */
public class RelDef implements Serializable{
	/**
	 * le schema de la relation (nom de la relation, nombre de colonne, types des colonnes)
	 */
	private RelSchema rS;
	/**
	 * id du fichier de la relation
	 */
	private int fileId;
	/**
	 * taille d'un record
	 */
	private int recordSize;
	/**
	 * nombre de cases
	 */
	private int slotCount;
	
	public RelDef(RelSchema rS, int fileId, int recordSize, int slotCount) {
		this.rS = rS;
		this.fileId = fileId;
		this.recordSize = recordSize;
		this.slotCount = slotCount;
	}

	public RelSchema getrS() {
		return rS;
	}

	public int getFileId() {
		return fileId;
	}
	
	public int getRecordSize() {
		return recordSize;
	}
	
	public int getSlotCount() {
		return slotCount;
	}
	
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	
	public void setrS(RelSchema rS) {
		this.rS = rS;
	}
	
	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}
	
	public void setSlotCount(int slotCount) {
		this.slotCount = slotCount;
	}
}
